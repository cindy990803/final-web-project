package com.project.bokduck.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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

    @Id@GeneratedValue
    private Long id;

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
}
