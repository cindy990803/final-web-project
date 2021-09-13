package com.project.bokduck.service;

import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.MemberType;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.util.MemberUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final DataSource dataSource;

    @PostConstruct  // 임시 계정 만들어 두고 테스트
    public void createTestMember() {
        memberRepository.save(Member.builder()
                .username("admin@test.com")
                .password(passwordEncoder.encode("1q2w3e4r!"))
                .tel("010-1234-1234")
                .joinedAt(LocalDateTime.now())
                .memberType(MemberType.ROLE_MANAGE)
                .build()
        );

        memberRepository.save(Member.builder()
                .username("test@test.com")
                .password(passwordEncoder.encode("1q2w3e4r!"))
                .tel("010-1234-1234")
                .joinedAt(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optional = memberRepository.findByUsername(username);
        Member member = optional.orElseThrow(
                () -> new UsernameNotFoundException("미등록 계정")
        );

        return  new MemberUser(member);
    }
}
