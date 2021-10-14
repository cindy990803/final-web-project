package com.project.bokduck.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.project.bokduck.domain.*;
import com.project.bokduck.multipart.fileUpLoadUtil;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final TagRepository tagRepository;

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    FileRepository fileRepository;

    /**
     * /write 요청시 리뷰 쓰기 뷰페이지로 리턴
     *
     * @param model
     * @param member
     * @return "post/review/write" 글쓰기 뷰페이지로 리턴
     * @author MunKyoung
     */
    @GetMapping("/write")
    public String write(Model model, @CurrentMember Member member) {
        if (member == null) {
            return "member/login";
        }
        model.addAttribute("WriteReviewVO", new WriteReviewVO());
        return "post/review/write";
    }

    /**
     * 뷰페이지에서 받아온 파라미터를 db에 저장후 리턴
     *
     * @param imageFile     multipart[]형으로 받아온 이미지 파일
     * @param pdfFile       multipart[]형으로 받아온 pdf 파일
     * @param member        현재 사용자 정보
     * @param writeReviewVO vo객체로 받아온 파라미터
     * @param file
     * @param model
     * @return
     * @throws IOException
     * @Author MunKyoung
     */
    @PostMapping("/write")
    @Transactional
    public String saveReview(

            @RequestParam("image") MultipartFile[] imageFile,

            @RequestParam("pdf") MultipartFile pdfFile,

            @CurrentMember Member member,

            @ModelAttribute WriteReviewVO writeReviewVO,

            File file, Model model) throws IOException {

        List<Image> imageList = new ArrayList<>();
        ReviewCategory reviewCategory = new ReviewCategory();
        Image image;

        if (!StringUtils.cleanPath(imageFile[0].getOriginalFilename()).equals("")) {


            for (int i = 0; i < imageFile.length; i++) {

                image = new Image();

                String imageName = StringUtils.cleanPath(imageFile[i].getOriginalFilename());

                image.setImageName(imageName);

                image = imageRepository.save(image);


                image.setImagePath("/review_images/" + image.getId() + "/" + imageName);

                String imageUploadDest = "review_images/" + image.getId();

                fileUpLoadUtil.saveFile(imageUploadDest, imageName, imageFile[i]);

                imageList.add(image);

                model.addAttribute("image", image);

            }

        }

        List<File> fileList = new ArrayList<>();

        String pdfName = StringUtils.cleanPath(pdfFile.getOriginalFilename());

        file.setFileName(pdfName);

        file = fileRepository.save(file);

        file.setFilePath("/file/" + file.getId() + "/" + pdfName);

        String pdfUploadDest = "file/" + file.getId();

        fileUpLoadUtil.saveFile(pdfUploadDest, pdfName, pdfFile);

        fileList.add(file);

        model.addAttribute("file", file);


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

        switch (writeReviewVO.getPayment()) {
            case "monthly":
                reviewCategory.setPayment(Payment.MONTHLY);
            case "charter":
                reviewCategory.setPayment(Payment.CHARTER);
            case "dealing":
                reviewCategory.setPayment(Payment.DEALING);
            case "halfCharter":
                reviewCategory.setPayment(Payment.HALFCHARTER);
                break;
            default:
        }

        List<Tag> tagList = new ArrayList<>();

        if (!writeReviewVO.getTags().isEmpty()) {
            JsonArray tagsJsonArray = new Gson().fromJson(writeReviewVO.getTags(), JsonArray.class);

            for (int i = 0; i < tagsJsonArray.size(); ++i) {

                JsonObject object = tagsJsonArray.get(i).getAsJsonObject();

                String tagValue = object.get("value").getAsString();

                Tag tag = Tag.builder()
                        .tagName(tagValue)
                        .build();

                tagList.add(tag);
            }
        }

        if (writeReviewVO.getTraffic() == null) {
            reviewCategory.setTraffic("");
        } else {
            reviewCategory.setTraffic(writeReviewVO.getTraffic());
        }
        if (writeReviewVO.getWelfare() == null) {
            reviewCategory.setWelfare("");
        } else {
            reviewCategory.setWelfare(writeReviewVO.getWelfare());
        }
        if (writeReviewVO.getConvenient() == null) {
            reviewCategory.setConvenient("");
        } else {
            reviewCategory.setConvenient(writeReviewVO.getConvenient());
        }
        if (writeReviewVO.getElectronicDevice() == null) {
            reviewCategory.setElectronicDevice("");
        } else {
            reviewCategory.setElectronicDevice(writeReviewVO.getElectronicDevice());
        }


        Review review1;
        review1 = reviewRepository.getById(member.getId());
        reviewCategory = reviewCategoryRepository.save(reviewCategory);

        Review review = Review.builder()
                .writer(member)
                .regdate(LocalDateTime.now())
                .address(writeReviewVO.getAddress())
                .detailAddress(writeReviewVO.getDetailAddress())
                .postCode(writeReviewVO.getPostCode())
                .extraAddress(writeReviewVO.getExtraAddress())
                .comment(writeReviewVO.getShortComment())
                .reviewCategory(reviewCategory)
                .reviewStatus(ReviewStatus.WAIT)
                .uploadImage(imageList)
                .star((writeReviewVO.getStars() / 2))
                .postName(writeReviewVO.getTitle())
                .tags(tagList)
                .postContent(writeReviewVO.getReviewComment())
                .build();

        if (!imageList.get(0).getImageName().equals("")) {
            review = Review.builder()
                    .uploadImage(imageList)
                    .build();
        }

        for (int i = 0; i < imageList.size(); i++) {
            imageList.get(i).setImageToPost(review);
        }
        for (int i = 0; i < fileList.size(); i++) {
            fileList.get(i).setFileToPost(review);
        }


        reviewRepository.save(review);

        for (Tag t : tagList) {
            if (t.getTagToPost() == null) {
                t.setTagToPost(new ArrayList<Post>());
            }
            t.getTagToPost().add(review);
        }


        return "index";
    }


    /**
     * @param pageable 리스트 페이징 처리
     * @param model
     * @param member   로그인한 사용자의 정보
     * @return 리뷰 리스트 페이지
     * @author 미리
     */
    @GetMapping("/list")
    public String reviewList(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             Model model,
                             @CurrentMember Member member) {

        reviewService.createLikeCount();

        Specification<Review> spec = ReviewSpecs.searchReviewStatus(ReviewStatus.COMPLETE);
        Page<Review> reviewList = reviewRepository.findAll(spec, pageable);
        model.addAttribute("reviewList", reviewList);

        if (member != null) {
            member = memberRepository.findById(member.getId()).orElseThrow();
        }
        model.addAttribute("member", member);

        int startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "post/review/list";
    }

    /**
     * @param pageable 리뷰 리스트 페이징 처리
     * @param model
     * @param vo       넘겨받은 파라미터 VO
     * @param member   로그인한 사용자의 정보
     * @return 리뷰 리스트 페이지
     * @author 미리
     */
    @GetMapping("/search")
    public String reviewSearch(@PageableDefault(size = 5) Pageable pageable,
                               Model model,
                               ReviewListVo vo,
                               @CurrentMember Member member) {

        reviewService.createLikeCount();

        if (member != null) {
            member = memberRepository.findById(member.getId()).orElseThrow();
        }
        model.addAttribute("member", member);

        Specification<Review> spec = ReviewSpecs.searchReviewStatus(ReviewStatus.COMPLETE);
        Page<Review> reviewList = null;
        Specification<ReviewCategory> categorySpec = null;
        Map<String, List<?>> map = new HashMap<>();

        if (vo.getRoomSize() != null && !vo.getRoomSize().isEmpty()) {
            map.put("roomSize", vo.getRoomSize());
        }

        if (vo.getStructure() != null && !vo.getStructure().isEmpty()) {
            map.put("structure", vo.getStructure());
        }

        if (vo.getPayment() != null && !vo.getPayment().isEmpty()) {
            map.put("payment", vo.getPayment());
        }

        if (vo.getTraffic() != null && !vo.getTraffic().isEmpty()) {
            map.put("traffic", vo.getTraffic());
        }

        if (vo.getConvenient() != null && !vo.getConvenient().isEmpty()) {
            map.put("convenient", vo.getConvenient());
        }

        if (vo.getWelfare() != null && !vo.getWelfare().isEmpty()) {
            map.put("welfare", vo.getWelfare());
        }

        if (vo.getElectronicDevice() != null && !vo.getElectronicDevice().isEmpty()) {
            map.put("electronicDevice", vo.getElectronicDevice());
        }

        categorySpec = ReviewSpecs.searchCategoryDetails(map);
        List<ReviewCategory> categoryList = reviewCategoryRepository.findAll(categorySpec);
        spec = spec.and(ReviewSpecs.searchCategory(categoryList));

        if (!vo.getAddress().isEmpty()) {
            // 지역 검색했을 때
            String[] search = {"address", "detailAddress", "postCode", "extraAddress"};
            Specification<Review> addressSpec = null;

            for (String s : search) {
                Map<String, Object> searchMap = new HashMap<>();
                searchMap.put(s, vo.getAddress().trim());
                addressSpec =
                        addressSpec == null ? ReviewSpecs.searchText(searchMap)
                                : addressSpec.or(ReviewSpecs.searchText(searchMap));
            }
            spec = spec.and(addressSpec);

        }

        if (!vo.getSearchText().isEmpty()) {
            // 검색창 사용 - 주소, 제목, 내용, 코멘트

            String[] search = {"postName", "postContent", "comment", "address", "detailAddress", "postCode", "extraAddress"};
            Specification<Review> searchSpec = null;


            for (String s : search) {
                Map<String, Object> searchMap = new HashMap<>();
                searchMap.put(s, vo.getSearchText().trim());
                searchSpec =
                        searchSpec == null ? ReviewSpecs.searchText(searchMap)
                                : searchSpec.or(ReviewSpecs.searchText(searchMap));
            }

            // 태그 검색하기

            Specification<Tag> tagSpec = ReviewSpecs.searchTagDetails(vo.getSearchText().trim());
            List<Tag> tagList = tagRepository.findAll(tagSpec);
            searchSpec = searchSpec.or(ReviewSpecs.searchTag(tagList));
            spec = spec.and(searchSpec);


        }

        if (vo.getPhotoReview() != null && !vo.getPhotoReview().isEmpty()) {
            // 포토리뷰 체크했을때
            spec = spec.and(ReviewSpecs.searchPhotoReview());
        }


        switch (vo.getLineUp()) { // 라인업체크했을때
            case "star": // 별점순
                pageable = PageRequest.of(vo.getPage(), 5, Sort.by("star").descending().and(Sort.by("id").descending()));
                reviewList = reviewRepository.findAll(spec, pageable);
                break;
            case "like": // 좋아요순
                pageable = PageRequest.of(vo.getPage(), 5, Sort.by("likeCount").descending().and(Sort.by("id").descending()));
                reviewList = reviewRepository.findAll(spec, pageable);
                break;
            default: // 최신순
                pageable = PageRequest.of(vo.getPage(), 5, Sort.by("id").descending());
                reviewList = reviewRepository.findAll(spec, pageable);
                break;
        }


        int startPage = Math.max(1, reviewList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(reviewList.getTotalPages(), reviewList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("reviewList", reviewList);

        return "post/review/list";
    }

    /**
     * @param id     해당 글의 id
     * @param member 로그인한 사용자의 정보
     * @return 리뷰 리스트에서 좋아요 눌렀을 때 AJAX 처리
     * @author 미리
     */
    @GetMapping("/list/like")
    @ResponseBody
    public String reviewListLike(Long id, @CurrentMember Member member) {

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
                likeCheck -= 1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요 완료!";
                likeCheck += 1;
                break;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        return jsonObject.toString();
    }

    @PostMapping("/read/like")
    @ResponseBody
    public String readLikeReview(Long id, @CurrentMember Member member) {
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
                likeCheck -= 1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요 완료!";
                likeCheck += 1;
                break;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        return jsonObject.toString();
    }

    /**
     * @param member
     * @param id
     * @param model
     * @return
     * @Author MunKyoung
     * 리뷰 수정 미완
     */
    @GetMapping("/modify")
    String modifyReview(@CurrentMember Member member,
                        Long id,
                        Model model) {

        id = 56l;


        Review review = reviewRepository.findAllById(id);
        ReviewCategory reviewCategory = reviewCategoryRepository.findAllByReviewId(review.getId());

        model.addAttribute("review", review);
        model.addAttribute("reviewCategory", reviewCategory);


        return "/post/review/modify";
    }


}