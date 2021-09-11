package com.project.bokduck.repository;

import domain.CommentReview;
import domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReviewRepository extends JpaRepository<CommentReview, Long> {
}
