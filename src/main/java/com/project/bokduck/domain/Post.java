package com.project.bokduck.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Getter @Setter @ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@SuperBuilder
@DynamicUpdate @DynamicInsert
public class Post {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String postName; // 게시물 제목

    @Column(nullable = false)
    private String postContent; // 게시글 내용

    private LocalDateTime regdate; // 작성일자

    private LocalDateTime updateDate; // 수정일자

    @ManyToOne
    private Member writer; // 작성자

    @ManyToMany(mappedBy = "likes",cascade = CascadeType.ALL)
    private List<Member> likers; // 좋아요 누른 사람들

    @ColumnDefault("0")
    private int hit; // 조회수

    @ManyToMany(mappedBy = "tagName",cascade = CascadeType.ALL)
    private List<Tag> tags; // 태그

    @OneToMany(mappedBy = "fileName", cascade = CascadeType.ALL)
    private List<File> uploadFile; // 업로드한 파일 - 계약서

    @OneToMany(mappedBy = "imageName", cascade = CascadeType.ALL)
    private List<Image> uploadImage; // 업로드한 이미지

    @PostLoad
    public void createList() {
        if (tags == null) tags = new ArrayList<>();
        if (uploadFile == null) uploadFile = new ArrayList<>();
        if (uploadImage == null) uploadImage = new ArrayList<>();
    }
}
