package com.project.bokduck.controller;

import com.project.bokduck.domain.CommentReview;
import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.Review;
import com.project.bokduck.repository.CommentReviewRepository;
import com.project.bokduck.repository.ReviewRepository;
import com.project.bokduck.service.ReviewService;
import com.project.bokduck.util.CurrentMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final ReviewRepository reviewRepository;
    private final CommentReviewRepository commentReviewRepository;
    private final ReviewService reviewService;

//    댓글 컨트롤러
//    @Transactional
//    @ResponseBody
//    @PostMapping("/review/read/comment")
//    public JSONObject addComment(@CurrentMember Member member, @RequestParam Long reviewId
//            , @RequestParam String content, Model model){
//        JSONObject jsonObject = new JSONObject();
//        Review review = reviewService.getReview(reviewId);
//
//        if(reviewService.saveComment(member, content, reviewId)){  // 삭제 성공하면
//            jsonObject.put("result", "success");
//            model.addAttribute("commentList",getListByBoard(review));
//        } else {
//            jsonObject.put("result", "fail");
//        }
//        return jsonObject;
//    }
//    private List<CommentReview> getListByBoard(Review review) throws RuntimeException{
//        return commentReviewRepository.getCommentOfReview(review);
//    }
//
//    // get, post, delete, put
//    //  https://www.bokduck.com/review/read/comment/delete?reviewId=13  ==> 파라미터
//    //   https://www.bokduck.com/review/read/comment/delete/13   ==> PathVariable
//    @Transactional
//    @ResponseBody
//    @PostMapping("/review/read/comment/delete") // reviewId=15
//    public JSONObject remove(@CurrentMember Member member, @RequestParam Long commentReviewId,
//                             Model model){
//        JSONObject jsonObject = new JSONObject();
//        CommentReview commentReview = commentReviewRepository.findById(commentReviewId).orElseThrow();
//
//        if(reviewService.deleteComment(member, commentReviewId)){  // 삭제 성공하면
//            jsonObject.put("result", "success");
//            model.addAttribute("commentList",getListByBoard(commentReview.getReview()));
//        } else {
//            jsonObject.put("result", "fail");
//        }
//        return jsonObject;
//    }
//
//    @Transactional
//    @ResponseBody
//    @PostMapping("/review/read/comment/modify")
//    public JSONObject modify(@CurrentMember Member member, @RequestParam Long commentReviewId
//                                , Model model) {
//        JSONObject jsonObject = new JSONObject();
//        CommentReview commentReview = commentReviewRepository.findById(commentReviewId).orElseThrow();
//
//        if (reviewService.modifyComment(member, commentReviewId)) {  // 수정 성공하면
//            jsonObject.put("result", "success");
//            model.addAttribute("commentList",getListByBoard(commentReview.getReview()));
//        } else {
//            jsonObject.put("result", "fail");
//        }
//        return jsonObject;
//    }
}
