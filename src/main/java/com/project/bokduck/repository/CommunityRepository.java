package com.project.bokduck.repository;


import com.project.bokduck.domain.Community;
import com.project.bokduck.domain.CommunityCategory;
import com.project.bokduck.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface CommunityRepository extends JpaRepository<Community, Long> {
   // Page<Community> findByCommunityCategory(CommunityCategory category, Pageable pageable);
    Page<Community> findByCommunityCategory(CommunityCategory tip, Pageable pageable);

    Page<Community> findAll(Specification<Community>specification, Pageable pageable);

    Page<Community> findByPostContentContaining(String searchText, Pageable pageable);


 /**
  * 맴버를 통해 커뮤니티 조회
  * @Author MunKyoung
  * @param writer
  * @param pageable
  * @return
  */
    Page<Community> findAllByWriter(Member writer,Pageable pageable);

    

 }
