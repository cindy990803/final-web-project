package com.project.bokduck.validation;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

/**
 * 회원가입 뷰에서 form의 파라미터를 받을 포장 객체
 */
@Data
public class JoinFormVo {

    @NotNull
    @NotBlank
    @Length(min=5, max=40, message="이메일은 5자 이상 40자 이하여야 합니다.")
    @Email(message = "이메일 형식을 지켜주세요. (예. you@test.com)")
    private String username;

    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$",
            message = "패스워드는 영문자, 숫자, 특수기호를 조합하여 최소 8자 이상을 입력하셔야 합니다."
    )
    private String password;

    private String passwordVerify;

    @NotNull
    @NotBlank
    @Length(min=2, max=10, message="닉네임은 2자 이상 10자 이하여야 합니다.")
    private String nickname;

    @Pattern(
            regexp = "^(?=.*[0-9]).{10,11}$",
            message = "개인 휴대전화는 \'-\' 없이 숫자만 입력해야 합니다."
    )
    private String tel;

    @NotNull
    @NotBlank
    @Pattern(regexp = "(^[0-9]+$|^$)")
    private String postcode;

    @NotNull
    @NotBlank
    private String baseAddress;

    @NotNull
    @NotBlank
    private String detailAddress;


    @AssertTrue(message = "주소란을 반드시 채워주세요.")
    public boolean isValidAddress(){
        return !postcode.isBlank() && !baseAddress.isBlank() && !detailAddress.isBlank();
    }

}