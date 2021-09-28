package com.project.bokduck.domain;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDate;
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
public class CommentReview {
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String text; // 댓글 내용

    @ManyToOne
    private Member writer; // 댓글 쓴 사람 // TODO nickname(String) -> writer(Member) 로 바뀜

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Review review; // 리뷰 게시판에씀? 이건뭐임..?

    @ManyToOne
    private CommentReview parent;

    @OneToMany(mappedBy ="parent" ,cascade = CascadeType.ALL)
    private List<CommentReview> children=new ArrayList<>(); // 대댓글, 댓글 글번호

    private LocalDateTime regdate; // 작성일자
}
