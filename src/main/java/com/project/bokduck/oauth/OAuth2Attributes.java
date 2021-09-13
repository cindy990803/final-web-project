package com.project.bokduck.oauth;

import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.MemberType;
import com.project.bokduck.domain.OAuthType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@Slf4j
public class OAuth2Attributes {
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String username;
    private final OAuthType oAuthType;



    public static OAuth2Attributes of(String registeredId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registeredId)) {
            return ofNaver("id", attributes);
        }
        if ("kakao".equals(registeredId)){
            return ofKakao("id",attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuth2Attributes ofNaver(String id, Map<String, Object> attributes) {

        log.info("Naver 로 로그인!");
        log.info("userNameAttributeName : {}", id);
        log.info("attributes : {}", attributes);
        log.info("attributes.get(\"response\") : {}", attributes.get("response"));

        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .username((String) response.get("email"))
                .oAuthType(OAuthType.NAVER)
                .attributes(response)
                .nameAttributeKey(id)
                .build();

    }

    private static OAuth2Attributes ofGoogle(String id, Map<String, Object> attributes) {
        log.info("Google 로 로그인!");
        log.info("userNameAttributeName : {}", id);
        log.info("attributes : {}", attributes);
        log.info("attributes.get(\"response\") : {}", attributes.get("response"));

        return OAuth2Attributes.builder()
                .username((String) attributes.get("email"))
                .oAuthType(OAuthType.GOOGLE)
                .attributes(attributes)
                .nameAttributeKey(id)
                .build();
    }
    private static OAuth2Attributes ofKakao(String id, Map<String, Object> attributes) {
        log.info("Kakao 로 로그인!");
        log.info("userNameAttributeName : {}", id);
        log.info("attributes : {}", attributes);
        log.info("attributes.get(\"response\") : {}", attributes.get("response"));

        return OAuth2Attributes.builder()
                .username((String) attributes.get("email"))
                .oAuthType(OAuthType.KAKAO)
                .attributes(attributes)
                .nameAttributeKey(id)
                .build();
    }

    public Member toMember(){
        return Member.builder()
                .username(username)
                .memberType(MemberType.ROLE_USER)
                .emailVerified(true)
                .password("{noop}")
                .oAuthType(oAuthType)
                .build();
    }
}
