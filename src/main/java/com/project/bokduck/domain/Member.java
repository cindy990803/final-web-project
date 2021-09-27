package com.project.bokduck.domain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
@DynamicInsert @DynamicUpdate
@Slf4j
public class Member {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String tel; // 핸드폰 번호

    @ColumnDefault("false")
    private boolean telVerified; // 핸드폰 인증 여부

    @Column(length = 47)
    private String nickname; // 닉네임

    @Embedded
    private UserAddress userAddress; // 주소

    private LocalDateTime joinedAt; // 가입일자

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ROLE_USER'")
    private MemberType memberType; // 회원 유형

    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL)
    private List<Post> write; // 글 쓴 목록

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Post> likes; // 좋아요 한 목록

    @ColumnDefault("false")
    private boolean nicknameOpen; // 닉네임 공개 여부

    @Enumerated(EnumType.STRING)
    private OAuthType oAuthType; // 제 3인증 타입

    @Column
    @ColumnDefault("false") // default false
    private boolean emailVerified;  // 이메일 인증 여부

    private String emailCheckToken; // 이메일 인증에 사용할 임의의 문자열

    @PostLoad
    public void createList() {
        log.info("PostLoad");
        if (likes == null) likes = new ArrayList<>();
        if (write == null) write = new ArrayList<>();
    }

    public void addLikeCommunity(Community community) {
        likes.add(community);
    }

}