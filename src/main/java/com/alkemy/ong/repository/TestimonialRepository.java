package com.alkemy.ong.repository;

import com.alkemy.ong.repository.model.TestimonialModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestimonialRepository extends JpaRepository<TestimonialModel, Long> {
}
