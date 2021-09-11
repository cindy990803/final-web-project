package com.project.bokduck.repository;

import domain.CommentCommunity;
import domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCommunityRepository extends JpaRepository<CommentCommunity, Long> {
}
