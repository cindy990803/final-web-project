package com.project.bokduck.service;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewService {
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;


    /**
     * @author 미리
     * 좋아요 갯수 새로고침하는 메서드
     */
    public void createLikeCount() {
        List<Review> reviewList = reviewRepository.findAll();
        for (Review r : reviewList) {
            r.setLikeCount(r.getLikers().size());
        }
        reviewRepository.saveAll(reviewList);
    }

    /**
     * @param id
     * @return 리뷰 레파지토리에서 id를 이용하여 해당되는 리뷰글 찾기
     * @author 미리
     */
    public Review findById(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }


    /**
     * @param member 로그인한 사용자의 정보
     * @param id     좋아요를 누른 글의 id
     * @return 좋아요를 눌렀을 때 해당되는 Enum
     * @author 미리
     */
    @Transactional
    public FlagLike addLike(Member member, Long id) {
        Review review;

        if (member == null) { // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e) {
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if (member.getLikes().contains(review)) { // 이미 좋아요한 상태일때
            member.getLikes().remove(review);
            return FlagLike.DUPLICATE;
        }

        // 좋아요 가능할때
        member.getLikes().add(review);
        return FlagLike.OK;
    }

    /**
     * @param id
     * @author 원재
     * 리뷰 조회수 DB에 리뷰글 볼떄마다 1씩 더해짐
     */
    public void increaseHit(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow();
        int hit = review.getHit();
        review.setHit(hit + 1);
        reviewRepository.save(review);
    }

    /**
     * @param member 로그인한 사용자의 정보
     * @param id     해당 글의 id
     * @return 승인 버튼을 눌렀을 때 해당되는 Enum
     * @author 미리
     */
    @Transactional
    public FlagLike addApproval(Member member, Long id) {
        Review review;

        if (member == null) { // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e) {
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if (review.getReviewStatus() == ReviewStatus.COMPLETE) { // 이미 승인 된 상태일때
            return FlagLike.DUPLICATE;
        }

        // 승인 가능할때
        review.setReviewStatus(ReviewStatus.COMPLETE);
        return FlagLike.OK;
    }

    /**
     * @param member 로그인한 사용자의 정보
     * @param id     해당 글의 id
     * @return 거부버튼을 눌렀을 때 해당되는 Enum
     * @author 미리
     */
    @Transactional
    public FlagLike addRefusal(Member member, Long id) {
        // 거부 버튼 눌렀을 때
        Review review;

        if (member == null) { // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e) {
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if (review.getReviewStatus() == ReviewStatus.REFUSAL) { // 이미 거부 된 상태일때
            return FlagLike.DUPLICATE;
        }

        // 거부 가능할때
        review.setReviewStatus(ReviewStatus.REFUSAL);
        return FlagLike.OK;
    }


    /**
     * 리뷰의 아이디로 리뷰글 삭제
     *
     * @param id
     * @return
     * @author 원재
     */
    public boolean deleteById(Long id) {
        reviewRepository.deleteById(id);

        return reviewRepository.findById(id) == null;
    }

    /**
     * @author 미리
     * 글의 상태에 따른 Enum
     * (예. 로그인이 필요함, 없는 게시물 등)
     */
    public enum FlagLike {
        ERROR_AUTH, ERROR_INVALID, DUPLICATE, OK
    }


}


