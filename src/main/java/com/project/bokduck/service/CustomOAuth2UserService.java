package com.project.bokduck.service;

import com.project.bokduck.domain.Member;
import com.project.bokduck.oauth.MemberOAuth2User;
import com.project.bokduck.oauth.OAuth2Attributes;
import com.project.bokduck.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;


    /**
     *@Author MunKyoung
     * @param userRequest
     * @return
     * @throws OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        String registeredId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        OAuth2Attributes attributes = OAuth2Attributes.of(
                registeredId, userNameAttributeName, oAuth2User.getAttributes()
        );

        Member member = saveOrUpdate(attributes);

        return new MemberOAuth2User(member, attributes);
    }

    /**
     * @Author MunKyoung
     * 이메일을 통해서 기존 사용자와 신규 사용자를 구별하여 로그인 시킴
     *
     * @param attributes
     * @return
     */
    private Member saveOrUpdate(OAuth2Attributes attributes) {
        String email = attributes.getUsername();
        Member member = memberRepository.findByUsername(email).map(entity -> {
            entity.setUsername(attributes.getUsername());
            return entity;
        }).orElse(attributes.toMember());

        member = memberRepository.save(member);
        memberService.login(member);
        return member;
    }
}