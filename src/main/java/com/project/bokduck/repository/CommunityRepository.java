package com.project.bokduck.repository;


import com.project.bokduck.domain.Community;
import com.project.bokduck.domain.CommunityCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findByCommunityCategory(CommunityCategory category, Pageable pageable);
}
