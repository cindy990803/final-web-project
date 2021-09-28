package com.project.bokduck.domain;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCommunity {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String text; // 댓글 내용

    @Column(nullable = false)
    private String nickname; // 댓글 쓴 사람

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Community community; // 커뮤니티 게시판에씀? 이건뭐임..?

    @ManyToOne
    private CommentCommunity parent;

    @OneToMany(mappedBy ="parent" ,cascade = CascadeType.ALL)
    private List<CommentCommunity> children=new ArrayList<>(); // 대댓글, 댓글 글번호

    private LocalDateTime regdate; // 작성일자
}
