package com.project.bokduck.repository;

import com.project.bokduck.domain.CommentReview;
import com.project.bokduck.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReviewRepository extends CrudRepository<CommentReview, Long> {
//
//    @Query("SELECT r FROM CommentReview r WHERE r.review = ?1 AND r.id > 0 ORDER BY r.id ASC")
//    List<CommentReview> getCommentOfReview(Review review);
//
//    void modifyById(Long commentReviewId);
}
