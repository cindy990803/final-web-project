package com.project.bokduck.repository;

import com.project.bokduck.domain.File;
import com.project.bokduck.domain.Image;
import com.project.bokduck.domain.ReviewCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, JpaSpecificationExecutor<Image> {
}