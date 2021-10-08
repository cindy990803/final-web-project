package com.project.bokduck.util;

import com.project.bokduck.domain.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class ReviewListVo {

    private int page;

    private String searchText;

    private String address;

    private List<RoomSize> roomSize;

    private List<Structure> structure;

    private List<Payment> payment;

    private List<String> traffic;

    private List<String> convenient;

    private List<String> welfare;

    private List<String> electronicDevice;

    private String photoReview;

    private String lineUp;


}
