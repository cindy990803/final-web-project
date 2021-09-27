package com.project.bokduck.controller;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import com.project.bokduck.service.ReviewService;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.util.WriteReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.JsonObject;
import com.project.bokduck.specification.ReviewSpecs;
import com.project.bokduck.util.ReviewListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import java.util.*;


@Controller
@RequestMapping("/review")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final MemberRepository memberRepository;
    private final ReviewCategory reviewCategory = new ReviewCategory();
    private final ImageRepository imageRepository;
    private final TagRepository tagRepository;

    @GetMapping("/writeReview")
    public String writeReview(Model model){
        model.addAttribute("WriteReviewVO",new WriteReviewVO());

        return "post/review/writeReview";
    }

    @PostMapping("/writeReview")
    public String saveReview(@CurrentMember Member member, @ModelAttribute WriteReviewVO writeReviewVO){



        switch (writeReviewVO.getRoomSize()){
            case "oneRoom":
                reviewCategory.setRoomSize(RoomSize.ONEROOM);
            case "twoRoom":
                reviewCategory.setRoomSize(RoomSize.TWOROOM);
            case "threeRoom":
                reviewCategory.setRoomSize(RoomSize.THREEMORE);
                break;
            default:
        }

        switch (writeReviewVO.getStructure()){
            case "villa":
                reviewCategory.setStructure(Structure.VILLA);
            case "office":
                reviewCategory.setStructure(Structure.OFFICE);
            case "apart":
                reviewCategory.setStructure(Structure.APART);
                break;
            default:
        }

        switch (writeReviewVO.getPayment()){
            case "monthly":
                reviewCategory.setPayment(Payment.MONTHLY);
            case "charter":
                reviewCategory.setPayment(Payment.CHARTER);
            case "dealing":
                reviewCategory.setPayment(Payment.DEALING);
                break;
            default:
        }

        switch (writeReviewVO.getElectronicDevice()){
            case "bed":
                reviewCategory.setElectronicDevice(ElectronicDevices.BED);
            case "airConditioner":
                reviewCategory.setElectronicDevice(ElectronicDevices.AIRCONDITIONER);
            case "washingMachine":
                reviewCategory.setElectronicDevice(ElectronicDevices.WASHINGMACHINE);
            case "refrigerator":
                reviewCategory.setElectronicDevice(ElectronicDevices.REFRIGERATOR);
                break;
            default:
        }

        switch (writeReviewVO.getWelfare()){
            case "parking":
                reviewCategory.setWelfare(Welfare.PARKING);
            case "deliveryBox":
                reviewCategory.setWelfare(Welfare.DELIVERYBOX);
            case "cctv":
                reviewCategory.setWelfare(Welfare.CCTV);
                break;
            default:
        }

        switch (writeReviewVO.getConvenient()){
            case "store":
                reviewCategory.setConvenient(Convenient.STORE);
            case "hospital":
                reviewCategory.setConvenient(Convenient.HOSPITAL);
            case "coinLaunder":
                reviewCategory.setConvenient(Convenient.COINLAUNDRY);
                break;
            default:
        }
        reviewCategoryRepository.save(reviewCategory);

        List<Image> imageList = new ArrayList<>();
        Image image = Image.builder()
                .imagePath(writeReviewVO.getFileImg())
                .build();


        imageRepository.save(image);


        Review review = Review.builder()
                .writer(member)
                .regdate(LocalDateTime.now())
                .address(writeReviewVO.getAddress())
                .detailAddress(writeReviewVO.getDetailAddress())
                .postCode(writeReviewVO.getPostCode())
                .extraAddress(writeReviewVO.getExtraAddress())
                .uploadImage(imageList)
                .comment(writeReviewVO.getShortComment())
                .reviewCategory(reviewCategory)
                .reviewStatus(ReviewStatus.WAIT)
                .star(0)


                .postName(writeReviewVO.getTitle())
                .postContent(writeReviewVO.getReviewComment())
                .build();

        reviewRepository.save(review);


        return "index";
    }


/*
    @GetMapping("/list")
    public String list(Model model, @PageableDefault(size = 2) Pageable pageable,
                       @RequestParam(required = false, defaultValue = "") String searchText) {
//        Page<Board> boards = boardRepository.findAll(pageable);
        Page<Board> boards = boardRepository.findByTitleContainingOrContentContaining(searchText, searchText, pageable);
        int startPage = Math.max(1, boards.getPageable().getPageNumber() - 4);
        int endPage = Math.min(boards.getTotalPages(), boards.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("boards", boards);
        return "board/list";
    }
 */

    @GetMapping("/list")
    public String reviewList(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model,
                             @CurrentMember Member member) {

        reviewService.createLikeCount();

        Specification<Review> spec = ReviewSpecs.searchReviewStatus(ReviewStatus.COMPLETE);
        Page<Review> reviewList = reviewRepository.findAll(spec, pageable);
        model.addAttribute("reviewList", reviewList);

        if(member!=null){
            member = memberRepository.findById(member.getId()).orElseThrow();
        }
        model.addAttribute("member", member);

        int startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        log.info("reviewList : {}", reviewList);
        return "post/review/list";
    }


    @GetMapping ("/search")
    public String reviewSearch(@PageableDefault(size = 5) Pageable pageable,
                               Model model,
                               ReviewListVo vo,
                               @CurrentMember Member member) {

        log.info("vo 페이지 : {}", vo.getPage());
        log.info("vo 포토리뷰 : {}", vo.getPhotoReview());

        reviewService.createLikeCount();

        if(member!=null){
            member = memberRepository.findById(member.getId()).orElseThrow();
        }
        model.addAttribute("member", member);

        Specification<Review> spec = ReviewSpecs.searchReviewStatus(ReviewStatus.COMPLETE);
        Page<Review> reviewList = null;
        Specification<ReviewCategory> categorySpec = null;
        Map<String, List<?>> map = new HashMap<>();

        if (vo.getRoomSize() != null) {
            map.put("roomSize", vo.getRoomSize());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getStructure() != null) {
            map.put("structure", vo.getStructure());
        }

        categorySpec = ReviewSpecs.searchCategoryDetails(map);
        List<ReviewCategory> categoryList = reviewCategoryRepository.findAll(categorySpec);
        spec = spec.and(ReviewSpecs.searchCategory(categoryList));
        reviewList = reviewRepository.findAll(spec, pageable);

         if (!vo.getAddress().isEmpty()){
             // 지역 검색했을 때
             String[] search = {"address","detailAddress","postCode","extraAddress"};
             Specification<Review> searchSpec = null;

             for (String s : search) {
                 Map<String, Object> searchMap = new HashMap<>();
                 searchMap.put(s, vo.getSearchText());
                 searchSpec =
                         searchSpec == null ? ReviewSpecs.searchText(searchMap)
                                 : searchSpec.or(ReviewSpecs.searchText(searchMap));
             }
             spec = spec.and(searchSpec);

             reviewList = reviewRepository.findAll(spec, pageable);

         }

         if (!vo.getSearchText().isEmpty()) {
            // 검색창 사용 - 주소, 제목, 내용, 코멘트

            String[] search = {"postName", "postContent", "comment","address", "detailAddress","postCode","extraAddress"};
            Specification<Review> searchSpec = null;


            for (String s : search) {
                Map<String, Object> searchMap = new HashMap<>();
                searchMap.put(s, vo.getSearchText());
                searchSpec =
                        searchSpec == null ? ReviewSpecs.searchText(searchMap)
                                : searchSpec.or(ReviewSpecs.searchText(searchMap));
            }

             // 태그 검색하기

             Specification<Tag> tagSpec = ReviewSpecs.searchTagDetails(vo.getSearchText());
             List<Tag> tagList = tagRepository.findAll(tagSpec);
             searchSpec = searchSpec.or(ReviewSpecs.searchTag(tagList));
             spec = spec.and(searchSpec);

            reviewList = reviewRepository.findAll(spec, pageable);

        }

        if (vo.getPhotoReview() != null) {
            // 포토리뷰 체크했을때
            List<Image> imageList = imageRepository.findAll();

            Image image = new Image();
            image.setImagePath("포토리뷰 없을때 체크용");
            List<Image> nullList = new ArrayList<>();
            nullList.add(image);
            imageRepository.saveAll(nullList);

            imageList = imageList.isEmpty() ? nullList : imageList ;


            spec = spec.and(ReviewSpecs.searchPhotoReview(imageList));
            reviewList = reviewRepository.findAll(spec, pageable);
        }

        if (vo.getLineUp() != null) {
            // 라인업 체크했을때
            switch (vo.getLineUp()) {
                case "star": // 별점순
                    Sort sort = Sort.by(Sort.Direction.DESC, "star")
                            .and(Sort.by(Sort.Direction.DESC, "id"));
                    pageable = PageRequest.of(0,5,Sort.by("star").descending().and(Sort.by("id").descending()));
                    reviewList = reviewRepository.findAll(spec,pageable);
                    break;
                case "like": // 좋아요순
                    pageable = PageRequest.of(0,5,Sort.by("likeCount").descending().and(Sort.by("id").descending()));
                    reviewList = reviewRepository.findAll(spec,pageable);
                    break;
                default: // 최신순
                    pageable = PageRequest.of(0,5,Sort.by("id").descending());
                    reviewList = reviewRepository.findAll(spec,pageable);
                    break;
            }
        }

        model.addAttribute("reviewList", reviewList);
        return "post/review/list";
    }



    @GetMapping("/list/like")
    @ResponseBody
    public String reviewListLike(Long id, @CurrentMember Member member) {
        // 좋아요 눌렀을 때
        log.info("좋아요 아이디 : {}", id);

        String resultCode = "";
        String message = "";

        // 좋아요 개수
        int likeCheck = reviewService.findById(id).getLikers().size();


        switch (reviewService.addLike(member, id)) {
            case ERROR_AUTH:
                resultCode = "error.auth";
                message = "로그인이 필요한 서비스입니다.";
                break;
            case ERROR_INVALID:
                resultCode = "error.invalid";
                message = "삭제된 게시물 입니다.";
                break;
            case DUPLICATE:
                resultCode = "duplicate";
                message = "좋아요 취소 완료!";
                likeCheck-=1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요 완료!";
                likeCheck+=1;
                break;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        log.info("jsonObject.toString() : {}", jsonObject.toString());

        return jsonObject.toString();
    }




}
