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
@Slf4j @RequiredArgsConstructor
public class ReviewService {
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ReviewRepository reviewRepository;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final CommentReviewRepository commentReviewRepository;
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final PlatformTransactionManager transactionManager;


    public void createLikeCount(){
        List<Review> reviewList = reviewRepository.findAll();
        for (Review r : reviewList){
            r.setLikeCount(r.getLikers().size());
        }
        reviewRepository.saveAll(reviewList);

        log.info("라이크 카운트 잘 나오니 ? : {}", findById(103l).getLikeCount());
    }

    public List<Review> findAll() {
        return reviewRepository.findAll();
    }

    public Review findById(Long id){
        return reviewRepository.findById(id).orElseThrow();
    }


    @Transactional
    public FlagLike addLike(Member member, Long id) {
        Review review;

        if(member == null){ // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e){
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if(member.getLikes().contains(review)){ // 이미 좋아요한 상태일때
            member.getLikes().remove(review);
            return FlagLike.DUPLICATE;
        }

        // 좋아요 가능할때
        member.getLikes().add(review);
        return FlagLike.OK;
    }


    public void increaseHit(Long id) {
       Review review = reviewRepository.findById(id).orElseThrow();
       int hit = review.getHit();
       review.setHit(hit+1);
       reviewRepository.save(review);
    }

    @Transactional
    public FlagLike addApproval(Member member, Long id) {
        // 승인 버튼 눌렀을 때
        Review review;

        if(member == null){ // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e){
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if(review.getReviewStatus() == ReviewStatus.COMPLETE){ // 이미 승인 된 상태일때
            return FlagLike.DUPLICATE;
        }

        // 승인 가능할때
        review.setReviewStatus(ReviewStatus.COMPLETE);
        return FlagLike.OK;
    }

    @Transactional
    public FlagLike addRefusal(Member member, Long id) {
        // 거부 버튼 눌렀을 때
        Review review;

        if(member == null){ // 로그인이 안되어있을때
            return FlagLike.ERROR_AUTH;
        }

        member = memberRepository.findById(member.getId()).orElseThrow();
        try {
            review = findById(id);
        } catch (NoSuchElementException e){
            return FlagLike.ERROR_INVALID; // 게시물이 삭제되었을 때
        }

        if(review.getReviewStatus() == ReviewStatus.REFUSAL){ // 이미 거부 된 상태일때
            return FlagLike.DUPLICATE;
        }

        // 거부 가능할때
        review.setReviewStatus(ReviewStatus.REFUSAL);
        return FlagLike.OK;
    }

    public boolean deleteById(Long id) {
        reviewRepository.deleteById(id);

        return reviewRepository.findById(id) == null;
    }



    public enum FlagLike {
        ERROR_AUTH, ERROR_INVALID, DUPLICATE, OK
    }

//    댓글
    public Review getReview(Long id) { return reviewRepository.findById(id).orElseThrow(); }


    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }


}


