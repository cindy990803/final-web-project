package com.project.bokduck.util;

import lombok.Builder;
import lombok.Data;

@Data
public class CommunityFormVo {

    private String postName;

    private String postContent;

    private String tags;

    private int communityCategory;

}
