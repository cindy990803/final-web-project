package com.project.bokduck.service;

import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.MemberType;
import com.project.bokduck.domain.OAuthType;
import com.project.bokduck.domain.UserAddress;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.util.MemberUser;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.project.bokduck.validation.JoinFormVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @PostConstruct  // 임시 계정 만들어 두고 테스트
    public void createTestMember() {
        memberRepository.save(Member.builder()
                .username("admin@test.com")
                .password(passwordEncoder.encode("1q2w3e4r!"))
                .tel("01011111111")
                .nickname("관리자")
                .nicknameOpen(true)
                .joinedAt(LocalDateTime.now())
                .memberType(MemberType.ROLE_MANAGE)
                .build()
        );

        memberRepository.save(Member.builder()
                .username("test@test.com")
                .password(passwordEncoder.encode("1q2w3e4r!"))
                .tel("01012341234")
                .nickname("test")
                .nicknameOpen(false)
                .joinedAt(LocalDateTime.now())
                .build()
        );

        List<Member> memberList = memberRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optional = memberRepository.findByUsername(username);
        Member member = optional.orElseThrow(
                () -> new UsernameNotFoundException("미등록 계정")
        );

        return  new MemberUser(member);
    }

    /**
     * 회원가입 프로세스 처리
     *  1. JoinFormVo 객체를 Member DB에 저장
     *  2. 이메일 보내기
     *  3. 로그인 처리해주기
     * @author 이선주
     * @param vo 회원가입 폼을 위한 VO
     */
    public void processNewMember(JoinFormVo vo) {
        Member member = saveNewMember(vo);
        emailService.sendEmail(member);
        login(member);
    }

    /**
     * JoinFormVo 객체를 Member DB에 저장
     * @author 이선주
     * @param vo 회원가입 폼을 위한 VO
     * @return 회원정보가 저장된 유저
     */
    private Member saveNewMember(JoinFormVo vo) {
        Member member = Member.builder()
                .username(vo.getUsername())
                .password(passwordEncoder.encode(vo.getPassword()))
                .tel(vo.getTel())
                .nickname(vo.getNickname())
                .joinedAt(LocalDateTime.now())
                .oAuthType(OAuthType.NONE)
                .memberType(MemberType.ROLE_USER)
                .userAddress(UserAddress.builder()
                        .postcode(vo.getPostcode())
                        .baseAddress(vo.getBaseAddress())
                        .detailAddress(vo.getDetailAddress()).build())
                .build();
        return memberRepository.save(member);
    }


    /**
     * 강제 로그인
     * @author 이선주
     * @param member
     */
    public void login(Member member) {
        MemberUser user = new MemberUser(member);

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        user,
                        user.getMember().getPassword(),
                        user.getAuthorities()
                );

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
    }

    public boolean containsTel(String tel) {
        boolean result;

        Optional<Member> member = memberRepository.findByTel(tel);
        if(member.get() != null) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    public String getEmail(String tel) {
        Optional<Member> member = memberRepository.findByTel(tel);
        String email = member.get().getEmail();

        return  email;
    }

    /**
     * 닉네임 중복 여부
     * @author 민경
     * @param newnickname 새로운 닉네임
     * @return 중복 유무에 따른 true, false 반환
     */
    public boolean checkNickname(String newnickname) {
       return memberRepository.existsBynickname(newnickname);
    }


}