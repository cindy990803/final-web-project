package com.project.bokduck.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.project.bokduck.domain.*;
import com.project.bokduck.repository.CommunityRepository;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.repository.ReviewRepository;
import com.project.bokduck.service.MemberService;
import com.project.bokduck.service.PassEmailService;
import com.project.bokduck.util.CommunityFormVo;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.util.WriteReviewVO;
import com.project.bokduck.validation.JoinFormValidator;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final MemberService memberService;
    private final PassEmailService passEmailService;


    @InitBinder("joinFormVo")
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(new JoinFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(Model model, @CurrentMember Member member) {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("joinFormVo", new JoinFormVo());
        return "/member/signup";
    }


    @PostMapping("/signup")
    public String signupSubmit(@Valid JoinFormVo joinFormVo, Errors errors) {
        log.info("joinFormVo : {}", joinFormVo);
        if (errors.hasErrors()) {
            log.info("회원가입 에러 : {}", errors.getAllErrors());
            return "/member/signup";
        }

        log.info("회원가입 정상!");

        memberService.processNewMember(joinFormVo);

        return "redirect:/";
    }

    @Transactional
    @GetMapping("/email-check")
    public String emailCheck(String username, String token, Model model) {

        Optional<Member> optional = memberRepository.findByUsername(username);
        boolean result;

        if (optional.isEmpty()) {
            result = false;
        } else if (!optional.get().getEmailCheckToken().equals(token)) {
            result = false;
        } else {
            result = true;
            optional.get().setEmailVerified(true);
        }

        String nickname = memberRepository.findByUsername(username).get().getNickname();

        model.addAttribute("nickname", nickname);
        model.addAttribute("result", result);

        return "member/email-check-result";
    }

    @GetMapping("/password")
    public String password() {
        return "member/password";
    }

    @PostMapping("/password")
    public String passwordSubmit(String username, Model model) {
        String message = "아이디 : ";
        Optional<Member> optional = memberRepository.findByUsername(username);
        if (optional.isEmpty()) {
            message = "아이디가 없습니다. 다시 한번 시도하세요.";

        } else {
            message = "임시 비밀번호가 발송되었습니다. 이메일을 확인해주세요.";
            passEmailService.sendPassEmail(optional.orElseThrow());
        }
        model.addAttribute("message", message);
        return "member/password";
    }

    @GetMapping("/review/list")
    public String reviewList() {
        return "post/review/list";
    }

    @GetMapping("/member/Test")
    public String Test() {
        return "member/Test";
    }

    @GetMapping("/community/write")
    public String communityWriteForm(Model model) {
        model.addAttribute("vo", new CommunityFormVo());
        return "post/community/write";
    }

    @PostMapping("/community/write")
    public String communityWriteSubmit(@CurrentMember Member member, CommunityFormVo vo, Model model) {

        //DB에 저장할 List<Tag>형 변수 설정
        List<Tag> tagList = new ArrayList<>();

        if (!vo.getTags().isEmpty()) {
            JsonArray tagsJsonArray = new Gson().fromJson(vo.getTags(), JsonArray.class);

            for (int i = 0; i < tagsJsonArray.size(); ++i) {
                JsonObject object = tagsJsonArray.get(i).getAsJsonObject();
                String tagValue = object.get("value").getAsString();

                Tag tag = Tag.builder()
                        .tagName(tagValue)
                        .build();

                tagList.add(tag);
            }
        }


        //DB에 저장할 CommunityCategory형 변수 설정
        CommunityCategory category = CommunityCategory.TIP;

        switch (vo.getCommunityCategory()) {
            case 0:
                category = CommunityCategory.TIP;
                break;
            case 1:
                category = CommunityCategory.INTERIOR;
                break;
            case 2:
                category = CommunityCategory.EAT;
                break;
            case 3:
                category = CommunityCategory.BOARD;
                break;
            default:
        }


        //데이터를 DB에 저장
        Community community = Community.builder()
                .postName(vo.getPostName())
                .postContent(vo.getPostContent())
                .regdate(LocalDateTime.now())
                .writer(member)
                .tags(tagList)
                .communityCategory(category)
                .build();

        communityRepository.save(community);

        return "index";  //TODO 커뮤니티글 보기 기능 완성 후 "post/community/read"로 바꾸기
    }
}


