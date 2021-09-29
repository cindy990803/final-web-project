package com.project.bokduck.controller;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import com.project.bokduck.service.ReviewService;
import com.project.bokduck.repository.FileRepository;
import com.project.bokduck.repository.ImageRepository;
import com.project.bokduck.repository.ReviewCategoryRepository;
import com.project.bokduck.repository.ReviewRepository;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.util.WriteReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.JsonObject;
import com.project.bokduck.specification.ReviewSpecs;
import com.project.bokduck.util.ReviewListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.FileOutputStream;
import java.io.InputStream;
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


    FileRepository fileRepository;

    /*@RequestMapping(value = {"/upload"}, method = {RequestMethod.POST}, consumes = MediaType.MULTIPART_FOR_DATA_VALUE)*/


    @GetMapping("/writeReview")
    public String writeReview(Model model) {
        model.addAttribute("WriteReviewVO", new WriteReviewVO());

        return "post/review/writeReview";
    }


    @PostMapping("/writeReview")
    public String saveReview(@RequestParam("file")MultipartFile file ,@CurrentMember Member member, @ModelAttribute WriteReviewVO writeReviewVO,RedirectAttributes redirectAttribute){

        if(file != null) {
            try (
                    // 맥일 경우
                    //FileOutputStream fos = new FileOutputStream("/tmp/" + file.getOriginalFilename());
                    // 윈도우일 경우
                    FileOutputStream fos = new FileOutputStream("D:\\final-web-project\\src\\main\\resources\\static\\saveImage" + file.getOriginalFilename());
                    InputStream is = file.getInputStream();
            ) {
                int readCount = 0;
                byte[] buffer = new byte[1024];
                while ((readCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, readCount);
                }
            } catch (Exception ex) {
                throw new RuntimeException("file Save Error");
            }


            if (writeReviewVO.getFile() != null) {
                List<Image> imageList = new ArrayList<>();
                Image image = Image.builder()
                        .build();
                imageRepository.save(image);
            }
        }



        switch (writeReviewVO.getRoomSize()) {
            case "oneRoom":
                reviewCategory.setRoomSize(RoomSize.ONEROOM);
            case "twoRoom":
                reviewCategory.setRoomSize(RoomSize.TWOROOM);
            case "threeRoom":
                reviewCategory.setRoomSize(RoomSize.THREEMORE);
                break;
            default:
        }

        switch (writeReviewVO.getStructure()) {
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


        reviewCategory.setTraffic(writeReviewVO.getTraffic());
        reviewCategory.setWelfare(writeReviewVO.getWelfare());
        reviewCategory.setConvenient(writeReviewVO.getConvenient());


        Review review = Review.builder()
                .writer(member)
                .regdate(LocalDateTime.now())
                .address(writeReviewVO.getAddress())
                .detailAddress(writeReviewVO.getDetailAddress())
                .postCode(writeReviewVO.getPostCode())
                .extraAddress(writeReviewVO.getExtraAddress())
                .comment(writeReviewVO.getConvenient())
                .reviewCategory(reviewCategory)
                .reviewStatus(ReviewStatus.COMPLETE) // todo wait으로 바꾸기
                .star(0)
                .postName(writeReviewVO.getTitle())
                .postContent(writeReviewVO.getReviewComment())
                .build();

        reviewCategoryRepository.save(reviewCategory);

        reviewRepository.save(review);
        return "index";
    }


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

        int startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 5);
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
        log.info("vo searchText : {}", vo.getSearchText());
        log.info("vo address : {}", vo.getAddress());

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

        if (vo.getPayment() != null) {
            map.put("payment", vo.getPayment());
        }

        if (vo.getTraffic() != null) {
            map.put("traffic", vo.getTraffic());
        }

        if (vo.getConvenient() != null) {
            map.put("convenient", vo.getConvenient());
        }

        if (vo.getWelfare() != null) {
            map.put("welfare", vo.getWelfare());
        }

        if (vo.getElectronicDevice() != null) {
            map.put("electronicDevice", vo.getElectronicDevice());
        }

        categorySpec = ReviewSpecs.searchCategoryDetails(map);
        List<ReviewCategory> categoryList = reviewCategoryRepository.findAll(categorySpec);
        spec = spec.and(ReviewSpecs.searchCategory(categoryList));
        reviewList = reviewRepository.findAll(spec, pageable);

        if (!vo.getAddress().isEmpty()){
            // 지역 검색했을 때
            String[] search = {"address","detailAddress","postCode","extraAddress"};
            Specification<Review> addressSpec = null;

            for (String s : search) {
                Map<String, Object> searchMap = new HashMap<>();
                searchMap.put(s, vo.getAddress());
                addressSpec =
                        addressSpec == null ? ReviewSpecs.searchText(searchMap)
                                : addressSpec.or(ReviewSpecs.searchText(searchMap));
            }
            spec = spec.and(addressSpec);

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
            spec = spec.and(ReviewSpecs.searchPhotoReview());
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


        int startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
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