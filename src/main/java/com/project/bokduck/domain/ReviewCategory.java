package com.project.bokduck.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@ToString
@EqualsAndHashCode(exclude = "review")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCategory {
    @Id @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "reviewCategory")
    private Review review;

    @Enumerated(EnumType.STRING)
    private RoomSize roomSize ;

    @Enumerated(EnumType.STRING)
    private Structure structure;

    @Enumerated(EnumType.STRING)
    private String payment;

    @Enumerated(EnumType.STRING)
    private String traffic;

   // @Enumerated(EnumType.STRING)
   // private Convenient convenient;
    private String convenient;

    @Enumerated(EnumType.STRING)
    private String welfare;

    @Enumerated(EnumType.STRING)
    private String electronicDevice;

}
