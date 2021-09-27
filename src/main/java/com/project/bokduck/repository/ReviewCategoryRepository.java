package com.project.bokduck.repository;

import com.project.bokduck.domain.Review;
import com.project.bokduck.domain.ReviewCategory;
import com.project.bokduck.domain.RoomSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewCategoryRepository extends JpaRepository<ReviewCategory, Long>, JpaSpecificationExecutor<ReviewCategory> {
}
