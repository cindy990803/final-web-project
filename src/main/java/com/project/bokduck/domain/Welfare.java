package com.project.bokduck.domain;

public enum Welfare {
    PARKING("주차"), CCTV("CCTV"), DELIVERYBOX("배달박스");

    private final String information;
    Welfare(String title){
        this.information = title;
    }
    public String getInformation(){
        return information;
    }
}
