package com.project.bokduck.repository;



import com.project.bokduck.domain.CommentCommunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentCommunityRepository extends JpaRepository<CommentCommunity, Long> {
    List<CommentCommunity> findByNickname(String nickname);
}
