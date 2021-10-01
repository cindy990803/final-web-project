package com.project.bokduck.domain;

public enum ElectronicDevices {
    BED("침대"), AIRCONDITIONER("에어컨"), WASHINGMACHINE("세탁기"), REFRIGERATOR("냉장고");

    private final String information;
    ElectronicDevices(String title){
        this.information = title;
    }
    public String getInformation(){
        return information;
    }
}
