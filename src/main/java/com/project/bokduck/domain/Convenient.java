package com.project.bokduck.domain;

public enum Convenient {
   STORE("상점"), HOSPITAL("병원"), COINLAUNDRY("코인빨래방");

   private final String information;
   Convenient(String title){
      this.information = title;
   }
   public String getInformation(){
      return information;
   }
}
