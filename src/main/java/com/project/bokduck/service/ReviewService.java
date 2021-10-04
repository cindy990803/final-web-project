package com.project.bokduck.service;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import com.project.bokduck.specification.ReviewSpecs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
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


    public enum FlagLike {
        ERROR_AUTH, ERROR_INVALID, DUPLICATE, OK
    }

//    댓글
    public Review getReview(Long id) { return reviewRepository.findById(id).orElseThrow(); }


    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    public boolean deleteComment(Member member, Long commentReviewId) {
        Optional<CommentReview> commentReviewOptional;
        CommentReview commentReview;

        if(commentReviewId == null || member == null){
            return false;
        }

        commentReviewOptional = commentReviewRepository.findById(commentReviewId);
        if (commentReviewOptional.isEmpty()){
            return false;
        }

        commentReview = commentReviewOptional.get();
        if(commentReview.getWriter() != member){
            return false;
        }

        commentReviewRepository.deleteById(commentReviewId);
        return true;
    }

    public boolean modifyComment(Member member, Long commentReviewId) {
        Optional<CommentReview> commentReviewOptional;
        CommentReview commentReview;

        if(commentReviewId == null || member == null){
            return false;
        }

        commentReviewOptional = commentReviewRepository.findById(commentReviewId);
        if (commentReviewOptional.isEmpty()){
            return false;
        }

        commentReview = commentReviewOptional.get();
        if(commentReview.getWriter() != member){
            return false;
        }

//        commentReviewRepository.modifyById(commentReviewId);
        return true;
    }

    public boolean saveComment(Member member, String content, Long reviewId) {
        CommentReview commentReview = new CommentReview();
        if(reviewId == null || member == null) {
            return false;
        }
        Review review = reviewRepository.findById(reviewId).orElseThrow();

        commentReview.setReview(review);
        commentReview.setText(content);
        commentReview.setWriter(member);

        commentReviewRepository.save(commentReview);
        return true;
    }
}


