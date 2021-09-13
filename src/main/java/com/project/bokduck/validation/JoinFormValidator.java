package com.project.bokduck.validation;

import com.project.bokduck.domain.Member;
import com.project.bokduck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;


@Component
@RequiredArgsConstructor
@Slf4j
public class JoinFormValidator implements Validator {

    private final MemberRepository memberRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(JoinFormVo.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        JoinFormVo vo = (JoinFormVo) target;
        Optional<Member> optional = memberRepository.findByUsername(vo.getUsername());
        if (optional.isPresent()) {
            errors.rejectValue(
                    "username",
                    "duplicate.email",
                    "이미 가입된 이메일입니다.");
            log.info("중복된 이메일 : {}", errors.getAllErrors());
        } else if (!(vo).getPassword().equals((vo).getPasswordVerify())) {
            errors.rejectValue("passwordVerify", "password.verify.failed", "비밀번호가 일치하지 않습니다.");
            log.info("비밀번호 불일치 : {}", errors.getAllErrors());
        }
    }
}

