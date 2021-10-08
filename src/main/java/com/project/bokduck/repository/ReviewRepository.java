package com.project.bokduck.repository;


import com.project.bokduck.domain.Post;
import com.project.bokduck.domain.Review;
import com.project.bokduck.domain.ReviewCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    // 리스트형으로 카테고리 받기
    Page<Review> findAllByReviewCategoryIn(List<ReviewCategory> categoryList, Pageable pageable);

    @Query(value = "SELECT * FROM POST LEFT OUTER JOIN REVIEW WHERE WRITER_ID = :writer",nativeQuery = true)
    List<Review> findALlByWriter(Long writer);
}

