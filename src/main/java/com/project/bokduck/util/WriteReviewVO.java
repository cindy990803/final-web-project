package com.project.bokduck.util;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WriteReviewVO  {

    private String title;

    private String address;

    private String detailAddress;

    private String postCode;

    private String extraAddress;

    private String reviewComment;

    private String reviewCategories;

    private String shortComment;

    private String roomSize;

    private String structure;

    private String traffic;

    private String welfare;

    private String convenient;

    private String electronicDevice;

    private String payment;

    private String Tags;

    private int stars;

  /*  private MultipartFile[] image;*/

}
