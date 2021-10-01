package com.project.bokduck.domain;

public enum Structure {
   VILLA("빌라"), OFFICE("오피스텔"), APART("아파트");

   private final String information;
   Structure(String title){
      this.information = title;
   }
   public String getInformation(){
      return information;
   }
}
