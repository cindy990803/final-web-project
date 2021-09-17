package com.project.bokduck.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
@SuperBuilder
public class Review extends Post{

    @Column(nullable = false)
    private String comment; // 한줄 코멘트

    @Column(nullable = false)
    private int star;

    @Enumerated(EnumType.STRING)
    private ReviewStatus reviewStatus;

    @OneToOne
    private Address address;

    @OneToMany(mappedBy = "name", cascade = CascadeType.ALL)
   private List<ReviewCategory> reviewCategories;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
   private List<CommentReview> commentReviews;

    @OneToMany(mappedBy = "fileName", cascade = CascadeType.ALL)
    private List<File> uploadFile; // 업로드한 파일 - 계약서

    @OneToMany(mappedBy = "imageName", cascade = CascadeType.ALL)
    private List<Image> uploadImage; // 업로드한 이미지

    @PostLoad
    public void createList() {
        if (uploadFile == null) uploadFile = new ArrayList<>();
        if (uploadImage == null) uploadImage = new ArrayList<>();
    }
}
