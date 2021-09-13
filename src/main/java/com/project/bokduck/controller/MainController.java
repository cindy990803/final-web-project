package com.project.bokduck.controller;

import com.project.bokduck.domain.Member;
import com.project.bokduck.util.CurrentMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {

    @RequestMapping("/")
    public String index(Model model, @CurrentMember Member member) {
        return "index";
    }

    @GetMapping("/login")
    public String login(){
        return "member/login";
    }

}
