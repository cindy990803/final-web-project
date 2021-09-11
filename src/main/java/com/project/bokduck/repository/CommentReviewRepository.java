package com.project.bokduck.repository;

import com.project.bokduck.domain.CommentReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReviewRepository extends JpaRepository<CommentReview, Long> {
}
