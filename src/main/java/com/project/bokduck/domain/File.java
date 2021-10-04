package com.project.bokduck.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {
    @Id @GeneratedValue
    private Long id;

    private String fileName;

    private String filePath;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post FileToPost; // 태그한 게시물

    public String getFilePath(){
        if(filePath == null) { return null; }

        return "/File/" + id + "/" + filePath;
    }

}
