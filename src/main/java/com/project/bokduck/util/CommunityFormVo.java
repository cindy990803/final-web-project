package com.project.bokduck.util;

import lombok.Builder;
import lombok.Data;

/**
 * 커뮤니티 입력 폼 VO
 * @author 이선주
 */
@Data
public class CommunityFormVo {

    private String postName;

    private String postContent;

    private String tags;

    private int communityCategory;

}
