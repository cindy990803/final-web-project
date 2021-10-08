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
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final PlatformTransactionManager transactionManager;



 //   @PostConstruct
 //  @DependsOn("memberRepository")
//    @Transactional
//    public void createTestReview(){ // 임시 리뷰글 만들기
//
//        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
//        tmpl.execute(new TransactionCallbackWithoutResult() {
//            @Override
//            protected void doInTransactionWithoutResult(TransactionStatus status) {
//
//                Long[] array = {1l,2l};
//
//                // 태그 만들어두기
//                List<Tag> tagList = new ArrayList<>(); // 임시태그 담아보자
//                String[] tagNameList = {"넓음", "깨끗함", "벌레없음"};
//
//                for(int i = 0; i < tagNameList.length; ++i){
//                    Tag tag = new Tag();
//                    tag.setTagName(tagNameList[i]);
//                    tagList.add(tag);
//                }
//
//                tagRepository.saveAll(tagList);
//
//
//                // 리뷰게시글을 만들어보자
//                List<Review> reviewList = new ArrayList<>();
//                ReviewCategory category = null;
//
//                for(int i = 0; i < 50; ++i){
//                    category = new ReviewCategory();
//                    if (i<=24){
//                        category.setRoomSize(RoomSize.ONEROOM);
//                        category.setStructure(Structure.VILLA);
//                    }else {
//                        category.setRoomSize(RoomSize.TWOROOM);
//                        log.info("????");
//                    }
//                    category = reviewCategoryRepository.save(category);
//
//                    Member member = memberRepository
//                            .findById(array[(int) (Math.random() * array.length)]).orElseThrow();
//
//                    Review review = Review.builder()
//                            .postName((i + 1) + "번 게시물")
//                            .postContent("어쩌구저쩌구")
//                            .writer(member)
//                            .comment("무난하다")
//                            .regdate(LocalDateTime.now())
//                            .hit((int) (Math.random() * 10))
//                            .star((int) (Math.random() * 5) + 1)
//                            .address("서울시 마포구 연희동 1-1")
//                            .detailAddress("XX빌라")
//                            .extraAddress("연희동")
//                            .reviewStatus(i % 2 == 0 ? ReviewStatus.WAIT : ReviewStatus.COMPLETE)
////                            .reviewCategory(category)
//                            .build();
//                    review.setReviewCategory(reviewCategoryRepository.findById((long)(i + 6)).get());
//                    reviewList.add(review);
//
//                }
//                reviewRepository.saveAll(reviewList);
//
//                // 태그 포스트에 넣기기
//                List<Tag> tag1 = tagRepository.findAll();
//                List<Post> tagPostList= postRepository.findAll();
//                for(Tag t : tag1){
//                    t.setTagToPost(tagPostList);
//                }
//
//                // 멤버 like 만들기
//                Member member = memberRepository.findById(1l).orElseThrow();
//                List<Post> likePostList = new ArrayList<>();
//                Post post = postRepository.findById(103l).orElseThrow();
//                likePostList.add(post);
//                member.setLikes(likePostList);
//
//                member = memberRepository.findById(2l).orElseThrow();
//                likePostList = postRepository.findAll();
//                member.setLikes(likePostList);
//            }
//        });
//
//    }


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

    public enum FlagLike {
        ERROR_AUTH, ERROR_INVALID, DUPLICATE, OK
    }
}

