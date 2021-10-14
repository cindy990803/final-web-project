package com.project.bokduck.repository;

import com.project.bokduck.domain.ReviewCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCategoryRepository extends JpaRepository<ReviewCategory, Long>, JpaSpecificationExecutor<ReviewCategory> {
    /**
     * @Author MunKyoung
     * review id 값으로 카테고리 조회
     * @param reviewId
     * @return
     */
    ReviewCategory findAllByReviewId(Long reviewId);
}
