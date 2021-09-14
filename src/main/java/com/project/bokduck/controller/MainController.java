package com.project.bokduck.controller;

import com.project.bokduck.domain.Member;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.service.MemberService;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.validation.JoinFormValidator;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @InitBinder("joinFormVo")
    protected void initBinder(WebDataBinder dataBinder){
        dataBinder.addValidators(new JoinFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(Model model, @CurrentMember Member member) {
        log.info("로그인 유저 : {}", member.getUsername());
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model){
        model.addAttribute("joinFormVo", new JoinFormVo());
        return "/member/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid JoinFormVo joinFormVo, Errors errors){
        log.info("joinFormVo : {}", joinFormVo);
        if(errors.hasErrors()){
            log.info("회원가입 에러 : {}", errors.getAllErrors());
            return "/member/signup";
        }

        log.info("회원가입 정상!");

        memberService.processNewMember(joinFormVo);

        return "redirect:/";
    }

    @Transactional
    @GetMapping("/email-check")
    public String emailCheck(String username, String token, Model model){

        Optional<Member> optional = memberRepository.findByUsername(username);
        boolean result;

        if(optional.isEmpty()){
            result = false;
        }
        else if(! optional.get().getEmailCheckToken().equals(token)){
            result = false;
        }
        else {
            result = true;
            optional.get().setEmailVerified(true);
        }

        String nickname = memberRepository.findByUsername(username).get().getNickname();

        model.addAttribute("nickname", nickname);
        model.addAttribute("result", result);

        return "member/email-check-result";
    }

}
