package com.project.bokduck.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id @GeneratedValue
    private Long id;

    private String imageName;

    @Column//(nullable = false)
    private String imagePath;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post imageToPost; // 태그한 게시물
}
