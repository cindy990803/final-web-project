package com.project.bokduck.domain;


public enum Payment {
    MONTHLY("월세"), HALFCHARTER("반전세"), CHARTER("전세"), DEALING("매매");

    private final String information;
    Payment(String title){
        this.information = title;
    }
    public String getInformation(){
        return information;
    }
}
