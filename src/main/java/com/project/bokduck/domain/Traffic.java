package com.project.bokduck.domain;

public enum Traffic {
    SUBWAY("지하철"), BUS("버스");

    private final String information;
    Traffic(String title){
        this.information = title;
    }
    public String getInformation(){
        return information;
    }
}
