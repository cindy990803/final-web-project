package com.project.bokduck.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tag {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String tagName; // 태그명

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Post> tagToPost; // 태그한 게시물

    @PostLoad
    public void createList() {
        if (tagToPost == null) tagToPost = new ArrayList<>();
    }
}