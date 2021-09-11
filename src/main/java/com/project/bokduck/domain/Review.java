package com.project.bokduck.domain;


import lombok.*;
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
@DiscriminatorValue("REVIEW")
public class Review extends Post{

    @Id@GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String comment;

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
