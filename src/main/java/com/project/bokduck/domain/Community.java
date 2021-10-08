package com.project.bokduck.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@SuperBuilder
@DynamicUpdate
@DiscriminatorValue("COMMUNITY")
public class Community extends Post implements Serializable {

    @Enumerated(EnumType.STRING)
    private CommunityCategory communityCategory;

    @OneToMany(mappedBy = "community" , cascade = CascadeType.ALL)
    private List<CommentCommunity> commentCommunity;

}