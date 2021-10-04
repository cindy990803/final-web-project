package com.project.bokduck.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.*;

import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DynamicInsert
@DynamicUpdate
public class Review extends Post {

    @Column(nullable = false)
    private String comment; // 한줄 코멘트

    @Column(nullable = false)
    private int star;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    private String address;

    private String detailAddress;

    private String postCode; //우편번호

    private String extraAddress; // (동)

    @ToString.Exclude
    @OneToOne(optional = false)
    private ReviewCategory reviewCategory;


    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<CommentReview> commentReviews;

    @OneToMany(mappedBy = "fileName", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<File> uploadFile; // 업로드한 파일 - 계약서

    @OneToMany(mappedBy = "imageName", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Image> uploadImage; // 업로드한 이미지

    public void createList() {
        if (uploadFile == null) uploadFile = new ArrayList<>();
        if (uploadImage == null) uploadImage = new ArrayList<>();
    }

//    @JsonIgnore
//    @OneToMany(mappedBy = "review", fetch = FetchType.LAZY)
//    private List<CommentReview> commentReviews;


}
