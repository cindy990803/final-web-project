package com.project.bokduck.repository;

import com.project.bokduck.domain.CommentReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReviewRepository extends JpaRepository<CommentReview, Long> {
    List<CommentReview> findByNickname(String nickname);
}
