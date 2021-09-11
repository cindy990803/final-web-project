package com.project.bokduck.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Welfare {
    @Id

    private Long id;

    @ManyToOne
    private ReviewCategory count;


}
