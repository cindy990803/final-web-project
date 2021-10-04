package com.project.bokduck.util;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
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

}
