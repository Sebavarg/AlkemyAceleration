package com.alkemy.ong.service;

import com.alkemy.ong.domain.Comment;
import com.alkemy.ong.exception.CommentNotFoundException;
import com.alkemy.ong.exception.NewsNotFoundException;
import com.alkemy.ong.exception.OperationNotPermittedException;
import com.alkemy.ong.exception.UserNotFoundException;
import com.alkemy.ong.mapper.CommentMapper;
import com.alkemy.ong.repository.CommentRepository;
import com.alkemy.ong.repository.NewsRepository;
import com.alkemy.ong.repository.UserRepository;
import com.alkemy.ong.repository.model.CommentModel;
import com.alkemy.ong.repository.model.NewsModel;
import com.alkemy.ong.repository.model.UserModel;
import com.alkemy.ong.security.MainUser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import static com.alkemy.ong.mapper.CommentMapper.mapModelToDomain;
import static com.alkemy.ong.security.SecurityUtils.getMainUser;


public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NewsRepository newsRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          NewsRepository newsRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional
    public Comment addComment(Comment comment) {
        CommentModel commentModel = CommentMapper.mapDomainToModel(comment);
        return mapModelToDomain(commentRepository.save(commentModel));
    }

    @Transactional(readOnly = true)
    public List<Comment> findAll() {
        return commentRepository.findAllByOrderByCreationDateAsc()
                .stream()
                .map(CommentMapper::mapModelToDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public Comment createComment(Comment comment) {
        MainUser mainUser = getMainUser();
        Optional<UserModel> user = userRepository.findById(mainUser.getId());
        Optional<NewsModel> news = newsRepository.findById(comment.getNewsId());
        if (!user.isPresent()) {
            throw new UserNotFoundException(String.format("User with ID: %s not found", comment.getUserId()));
        }
        if (!news.isPresent()) {
            throw new NewsNotFoundException(String.format("News with ID: %s not found", comment.getNewsId()));
        }
        comment.setUserId(mainUser.getId());
        return mapModelToDomain(commentRepository.save(CommentMapper.mapDomainCreationToModel(comment)));
    }

    @Transactional
    public void deleteComment(Long id) throws CommentNotFoundException {
        Optional<CommentModel> commentModelOptional = commentRepository.findById(id);
        if (!commentModelOptional.isEmpty()) {
            CommentModel commentModel = commentModelOptional.get();
            commentRepository.delete(commentModel);
        } else {
            throw new CommentNotFoundException(String.format("Comment with ID: %s not found", id));
        }
    }

    @Transactional
    public Comment updateComment(Long commentId, Comment commentUpdate)
            throws CommentNotFoundException, OperationNotPermittedException {
        MainUser mainUser = getMainUser();
        Optional<CommentModel> commentModel = commentRepository.findById(commentId);
        if (!commentModel.isPresent()) {
            throw new CommentNotFoundException(String.format("Comment with ID: %s not found", commentId));
        }
        CommentModel comment = commentModel.get();
        if (!hasValidId(mainUser, comment) && !isAdmin(mainUser)) {
            throw new OperationNotPermittedException("Invalid user");
        }
        comment.setBody(commentUpdate.getBody());
        return mapModelToDomain(commentRepository.save(comment));
    }

    @Transactional
    public List<Comment> getAllComment(Long id) throws NewsNotFoundException {
        Optional<NewsModel> newsModel = newsRepository.findById(id);
        if (newsModel.isPresent()) {
            List<CommentModel> commentModelList = commentRepository.findByNewsId(newsModel.get().getId());
            return commentModelList.stream().map(CommentMapper::mapModelToDomain).collect(Collectors.toList());
        } else {
            throw new NewsNotFoundException(String.format("News with ID: %s not found", id));
        }
    }

    private boolean hasValidId(MainUser mainUser, CommentModel commentModel) {
        return mainUser.getId() == commentModel.getUserId();
    }

    private boolean isAdmin(MainUser mainUser) {
        return mainUser.getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
    }
}
