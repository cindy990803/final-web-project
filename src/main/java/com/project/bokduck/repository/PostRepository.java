package com.project.bokduck.repository;


import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByWriter(Member member);

    List<Post> findBylikers(Member member);

    List<Post> findByVisitedMember(Member member);
}
