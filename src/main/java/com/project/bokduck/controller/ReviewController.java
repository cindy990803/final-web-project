package com.project.bokduck.controller;

import com.project.bokduck.domain.*;
import com.project.bokduck.repository.ImageRepository;
import com.project.bokduck.repository.ReviewCategoryRepository;
import com.project.bokduck.repository.ReviewRepository;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.util.WriteReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/review")
@Slf4j
@RequiredArgsConstructor

public class ReviewController {

    private final ReviewCategoryRepository reviewCategoryRepository;

    private final ReviewCategory reviewCategory = new ReviewCategory();

    private final ReviewRepository reviewRepository;

    private final ImageRepository imageRepository;




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

}
