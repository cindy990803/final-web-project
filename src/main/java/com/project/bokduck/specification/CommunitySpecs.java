package com.project.bokduck.specification;


import com.project.bokduck.domain.Community;
import com.project.bokduck.domain.Post;
import com.project.bokduck.domain.Review;
import com.project.bokduck.domain.Tag;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommunitySpecs {


    public static Specification<Community> findCategory(Map<String, Object> filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.forEach((key, value) -> {
                String likeValue = "%" + value + "%";

                switch (key) {
                    case "EAT":  case "TIP":
                        predicates.add(criteriaBuilder.like(root.get(key).as(String.class), likeValue));
                        break;
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }



    public static Specification<Community> searchText(Map<String, Object> filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.forEach((key, value) -> {
                String likeValue = "%" + value + "%";

                switch (key) {
                    case "postName":  case "postContent":
                        predicates.add(criteriaBuilder.like(root.get(key).as(String.class), likeValue));
                        break;
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Tag> searchTagDetails(String tagName){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            String likeValue = "%" + tagName + "%";
            predicates.add(criteriaBuilder.like(root.get("tagName"), likeValue));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Community> searchTag(List<Tag> tags){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Tag e : tags) {
                predicates.add(criteriaBuilder.isMember(tags, root.get("tags")));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));

        };
    }


}
