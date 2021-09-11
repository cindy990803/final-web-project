package com.project.bokduck.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@DynamicUpdate
public class Address {

    @Id
    private Long id;

    @Column
    private String specialCity;

    @Column
    private String specialSelfCity ;

    @Column
    private String specialSelfDo;

    @Column
    private String etropolitanCity;

    @Column
    private String doo;

    @Column
    private String gu;

    @Column
    private String si;

    @Column
    private String goon;

    @Column
    private String dong;

    @Column
    private String eub;

    @Column
    private String myeon;

}
