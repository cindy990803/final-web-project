package com.project.bokduck.domain;

public enum RoomSize {
   ONEROOM("원룸"), TWOROOM("투룸"), THREEMORE("쓰리룸 이상");

   private final String information;
   RoomSize(String title){
      this.information = title;
   }
   public String getInformation(){
      return information;

   }
}
