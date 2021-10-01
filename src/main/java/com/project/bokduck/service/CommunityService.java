package com.project.bokduck.service;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.CommunityRepository;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.repository.PostRepository;
import com.project.bokduck.repository.TagRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.DependsOn;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final MemberRepository memberRepository;
    private final PlatformTransactionManager transactionManager;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;


    public List<Community> findAll() {
        return communityRepository.findAll();
    }

    public Community findCommunityId(Long id) {
        return communityRepository.findById(id).orElseThrow();
    }


    @Transactional
    public FlagLike addLike(Member member, Long id) {
        Community community = findCommunityId(id);

        if (member == null) { //로그인이 안 되어 있을 때
            return FlagLike.ERROR_AUTH;
        }
        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            community = findCommunityId(id);
        } catch (NoSuchElementException e) {
            return FlagLike.ERROR_INVALID; //게시물이 삭제 되었을때
        }
        if (member.getLikes().contains(findCommunityId(id))) { //이미 찜한 항태 일때
            member.getLikes().remove(findCommunityId(id));
            return FlagLike.ERROR_DUPLICATE;

        }

        member.addLikeCommunity(community); //정상일때
        return FlagLike.OK;
    }

    public void createLikeCount(){
        List<Community> communityList = communityRepository.findAll();
        for (Community c : communityList){
            c.setLikeCount(c.getLikers().size());
        }
        communityRepository.saveAll(communityList);
    }

    public Page<Community> findPage(Pageable pageable) {
        return communityRepository.findAll(pageable);
    }

    public Page<Community> findCommunityCategoryPage(CommunityCategory tip, Pageable pageable) {
        return communityRepository.findByCommunityCategory(tip, pageable);
    }

    public enum FlagLike {
        ERROR_AUTH, ERROR_INVALID, ERROR_DUPLICATE, OK
    }
}