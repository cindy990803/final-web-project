package com.project.bokduck.service;

import com.project.bokduck.domain.Member;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendEmail(Member member) {
        //TOKEN 생성
        String token = UUID.randomUUID().toString();

        //TOKEN을 DB에 update
        member.setEmailCheckToken(token);

        //심플메일 보내기
        sendSimpleMailMessage(member);
    }

    private void sendSimpleMailMessage(Member member) {
        // 이메일 날리기
        String url =
                "http://localhost:8080/email-check?username=" + member.getUsername()
                        + "&token=" + member.getEmailCheckToken();

        String title = "[복덕복덕] 회원 가입에 감사드립니다. 딱 한 가지 과정이 남았습니다.";
        String message = "다음 링크를 브라우저에 붙여넣어주세요. 링크 : " + url;
        String sender = "bokduck-admin@bokduck.com";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(member.getUsername());
        mailMessage.setSubject(title);
        mailMessage.setText(message);
        mailMessage.setFrom(sender);
        javaMailSender.send(mailMessage);

        log.info("\nTo. {}\nTitle : {} \n{}\nFrom.{}",
                mailMessage.getTo(),
                mailMessage.getSubject(),
                mailMessage.getText(),
                mailMessage.getFrom());
    }


}
