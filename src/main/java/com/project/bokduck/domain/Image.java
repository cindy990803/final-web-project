package com.project.bokduck.domain;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {
    @Id @GeneratedValue
    private Long id;

    private String imageName;

    private String imagePath;

    @ManyToOne(cascade = CascadeType.ALL)
    private Post imageToPost; // 태그한 게시물

    public String getImagePath(){
        if (imagePath == null){return null;}

        return "/review_images/" + id +"/"+ imageName;

    }

}
