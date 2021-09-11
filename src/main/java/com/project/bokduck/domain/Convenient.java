package com.project.bokduck.domain;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Convenient {
    @Id
    private Long id;

    @ManyToOne
    private ReviewCategory count;


}
