package com.project.bokduck.util;

import com.project.bokduck.domain.Member;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommunityViewVo {
    private String postName;

    private int hit;

    private int likersCount;
}

