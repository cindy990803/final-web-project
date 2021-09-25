
package com.project.bokduck.service;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
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


    @PostConstruct
    @DependsOn("memberRepository")
    @Transactional
    public void createTestReview() { // 임시 리뷰글 만들기

        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Long[] array = {1l, 2l};

                // 태그 만들어두기
                List<Tag> tagList = new ArrayList<>(); // 임시태그 담아보자
                String[] tagNameList = {"넓음", "깨끗함", "벌레없음"};

                for (int i = 0; i < tagNameList.length; ++i) {
                    Tag tag = new Tag();
                    tag.setTagName(tagNameList[i]);
                    tagList.add(tag);
                }

                tagRepository.saveAll(tagList);


                // 리뷰게시글을 만들어보자
                List<Review> reviewList = new ArrayList<>();
                ReviewCategory category = null;
                for (int i = 0; i < 50; ++i) {
                    category = new ReviewCategory();
                    if (i <= 24) {
                        category.setRoomSize(RoomSize.ONEROOM);
                        category.setStructure(Structure.VILLA);
                    } else {
                        category.setRoomSize(RoomSize.TWOROOM);
                        log.info("????");
                    }
                    category = reviewCategoryRepository.save(category);

                    Member member = memberRepository
                            .findById(array[(int) (Math.random() * array.length)]).orElseThrow();
                    Review review = Review.builder()
                            .postName((i + 1) + "번 게시물")
                            .postContent("어쩌구저쩌구")
                            .writer(member)
                            .comment("무난하다")
                            .regdate(LocalDateTime.now())
                            .hit((int) (Math.random() * 10))
                            .star((int) (Math.random() * 5) + 1)
                            .reviewStatus(i > 24 ? ReviewStatus.COMPLETE : ReviewStatus.WAIT)
//                          .reviewCategory(category)
                            .build();
                    review.setReviewCategory(reviewCategoryRepository.findById((long) (i + 6)).get());
                    reviewList.add(review);


                }
                reviewRepository.saveAll(reviewList);

                // 태그 포스트에 넣기기
                List<Tag> tag1 = tagRepository.findAll();
                List<Post> tagPostList = postRepository.findAll();
                for (Tag t : tag1) {
                    t.setTagToPost(tagPostList);
                }

            }
        });

    }
}
