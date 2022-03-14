package com.alkemy.ong.controller;

import com.alkemy.ong.domain.News;
import com.alkemy.ong.dto.ErrorDTO;
import com.alkemy.ong.dto.NewsDTO;
import com.alkemy.ong.dto.NewsListDTO;
import com.alkemy.ong.dto.NewsUpdateDTO;
import com.alkemy.ong.exception.NewsNotFoundException;
import com.alkemy.ong.mapper.NewsMapper;
import com.alkemy.ong.service.NewsService;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsDTO> getById(@PathVariable Long id) throws NewsNotFoundException {
        return ResponseEntity.ok(NewsMapper.mapDomainToDTO(newsService.getById(id)));
    }

    @ExceptionHandler(NewsNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNewsNotFoundExceptions(NewsNotFoundException ex) {
        ErrorDTO newsNotFound =
                ErrorDTO.builder()
                        .code(HttpStatus.NOT_FOUND)
                        .message(ex.getMessage()).build();
        return new ResponseEntity(newsNotFound, HttpStatus.NOT_FOUND);

    }

    @PutMapping("{id}")
    public ResponseEntity<NewsDTO> updateNews(@PathVariable Long id,
                                              @RequestBody NewsUpdateDTO newsUpdateDTO) throws NewsNotFoundException {
        News news = NewsMapper.mapUpdateDTOToDomain(newsUpdateDTO);
        NewsDTO newsUpdated = NewsMapper.mapDomainToDTO(newsService.updateNews(id, news));
        return ResponseEntity.ok().body(newsUpdated);
    }

    @PostMapping
    public ResponseEntity<NewsDTO> createNews(@Valid @RequestBody NewsDTO newsDTO) {
        News news = newsService.createNews((NewsMapper.mapDTOToDomain(newsDTO)));
        NewsDTO result = NewsMapper.mapDomainToDTO(news);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) throws NewsNotFoundException {
        newsService.deleteNews(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<NewsListDTO> getAll(@RequestParam(defaultValue = "0") Integer page) {
        NewsListDTO response = new NewsListDTO();
        String currentContextPath = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        Page<News> news = newsService.getAll(page);
        if (news.hasNext()) {
            response.setNextPage(currentContextPath.concat(String.format("/news?page=%d", page + 1)));
        }
        if (news.hasPrevious()) {
            response.setPreviousPage(currentContextPath.concat(String.format("/news?page=%d", page - 1)));
        }
        response.setNews(news.getContent().stream().map(NewsMapper::mapDomainToDTO).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }
}
