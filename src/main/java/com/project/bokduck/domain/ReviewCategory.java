package com.project.bokduck.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCategory {
    @Id
    private Long id;

    @ManyToOne
    private Review name;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<RoomSize> roomSize;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<Structure> structure;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<Payment> payments;


    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<Traffic> traffic;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<Convenient> convenient;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<Welfare> welfare;

    @OneToMany(mappedBy = "count", cascade = CascadeType.ALL)
    private List<ElectronicDevices> electronicDevice;

    @PostLoad
    public void createList() {
        if (roomSize == null)  roomSize= new ArrayList<>();
        if (structure == null)  structure= new ArrayList<>();
        if (traffic == null)  traffic= new ArrayList<>();
        if (convenient == null)  convenient= new ArrayList<>();
        if (welfare == null)  welfare= new ArrayList<>();
        if (electronicDevice == null)  electronicDevice= new ArrayList<>();
        if (payments == null)  payments= new ArrayList<>();

    }

    }
