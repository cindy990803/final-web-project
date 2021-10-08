package com.project.bokduck.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCommunity implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String text; // 댓글 내용

    @Column(nullable = false)
    private String nickname; // 댓글 쓴 사람 닉네임

    @Column(nullable = false)
    private boolean nicknameOpen; // 댓글 쓴 사람 닉네임 공개여부

    @ManyToOne
    private Community community; // 댓글 쓴 게시글

//    @Column(columnDefinition = "VARBINARY(MAX)")
//    private CommentCommunity parent;

    private long parentId;

    private LocalDateTime regdate; // 작성일자
}
