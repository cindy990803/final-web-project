package com.project.bokduck.domain;


import lombok.*;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder
public class Community extends Post{

    @Enumerated()
    private CommunityCategory communityCategory;

    @OneToMany(mappedBy = "community" , cascade = CascadeType.ALL)
    private List<CommentCommunity> commentCommunity;

}
