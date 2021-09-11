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

    @ManyToMany(cascade = CascadeType.ALL)
    @Column(nullable = false)
    private List<Post> tagName; // 태그명

    @PostLoad
    public void createList() {
        if (tagName == null) tagName = new ArrayList<>();
    }
}
