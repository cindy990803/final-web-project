package com.project.bokduck.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter @Setter @ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserAddress {

    private String postcode; // 우편번호

    private String baseAddress; // 기본주소

    private String detailAddress; // 상세주소
}
