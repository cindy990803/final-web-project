package com.project.bokduck.util;

import com.project.bokduck.domain.Member;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class MemberUser extends User {

    private final Member member;

    public MemberUser(Member member){
        super(member.getUsername(),
                member.getPassword(),
                List.of(new SimpleGrantedAuthority(member.getMemberType().name())) //권한!!
        );
        this.member=member;
    }


}
