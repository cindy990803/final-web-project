package com.project.bokduck.service;

import com.project.bokduck.domain.Member;
import com.project.bokduck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.SecureRandom;

@Slf4j
@Service
@RequiredArgsConstructor
/**
 * @author 민경
 *  임시 비밀번호를 랜덤으로 조합하여 이메일로 임시 비밀번호를 보내준다.
 */
public class PassEmailService {
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendPassEmail(Member member) {
        sendHtmlMailMessage(member);

    }

    public static String randomPw(){
        char pwCollection[] = new char[] {
                '1','2','3','4','5','6','7','8','9','0',
                'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                '!','@','#','$','%','^','&','*','(',')'};

        String ranPw = "";

        for (int i = 0; i < 10; i++) {
            int selectRandomPw = (int)(Math.random()*(pwCollection.length));
            ranPw += pwCollection[selectRandomPw];
        }
        return ranPw;
    }
    private void sendHtmlMailMessage(Member member){
        String ranPw = randomPw();

        String html = "<html><body>" +"임시 비밀번호"+ranPw+
                "<p style=\"background:white\">링크 : <a href=\"http://localhost:8080/login"
                + "\">로그인 원하시는 경우 이곳을 클릭하세요.</a></p>" +
                "</body></html>";
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,false,"UTF-8");

            String title = "[Bokduck] 임시 비밀번호 발송";
            mimeMessageHelper.setTo(member.getUsername());
            mimeMessageHelper.setSubject(title);
            mimeMessageHelper.setText(html, true);

            member.setPassword(passwordEncoder.encode(ranPw));
            javaMailSender.send(mimeMessage);

        }catch (MessagingException e){
            log.error("이메일 전송 실패. ({})", member.getUsername());
        }

    }


}
