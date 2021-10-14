package com.project.bokduck.specification;

import com.project.bokduck.domain.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ReviewSpecs {

    /**
     * @author 미리
     * @return 포토리뷰가 있는 리뷰로만 적용시키는 검색조건
     */
    public static Specification<Review> searchPhotoReview(){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotEmpty(root.get("uploadImage")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    /**
     * @author 미리
     * @param reviewStatus 해당 리뷰글에 대한 상태
     * @return 해당되는 리뷰 글 상태(승인,거부,보류)에 대해서만 리뷰에 적용시키는 검색조건
     */
    public static Specification<Review> searchReviewStatus(ReviewStatus reviewStatus){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("reviewStatus"), reviewStatus));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    /**
     * @author 미리
     * @param category 해당 리뷰글에 대한 카테고리 리스트
     * @return 해당되는 리뷰 카테고리들에 대해서만 리뷰에 적용시키는 검색조건
     */
    public static Specification<Review> searchCategory(List<ReviewCategory> category){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (ReviewCategory e : category) {
                predicates.add(criteriaBuilder.equal(root.get("reviewCategory"), e));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * @author 미리
     * @param filter 각각의 리뷰카테고리에 적용시킬 필드들과 선택된 카테고리 리스트로 구성된 map
     * @return 해당되는 카테고리들을 검색하는 카테고리 검색조건
     */
    public static Specification<ReviewCategory> searchCategoryDetails(Map<String, List<?>> filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> roomSizePredicates = new ArrayList<>();
            List<Predicate> structurePredicates = new ArrayList<>();
            List<Predicate> paymentPredicates = new ArrayList<>();
            List<Predicate> trafficPredicates = new ArrayList<>();
            List<Predicate> electronicDevicePredicates = new ArrayList<>();
            List<Predicate> welfarePredicates = new ArrayList<>();
            List<Predicate> convenientPredicates = new ArrayList<>();


            filter.forEach((key, value) -> {

                switch (key) {
                    case "roomSize":
                        for (RoomSize e : (List<RoomSize>)value) {
                            roomSizePredicates.add(criteriaBuilder.equal(root.get(key), e));
                        }

                        break;

                    case "structure":
                        for (Structure e : (List<Structure>)value) {
                            structurePredicates.add(criteriaBuilder.equal(root.get(key), e));
                        }
                        break;

                    case "payment":
                        for (Payment e : (List<Payment>)value) {
                            paymentPredicates.add(criteriaBuilder.equal(root.get(key), e));
                        }
                        break;

                    case "traffic":
                        for (String e : (List<String>)value) {
                            String likeValue = "%"+e+"%";
                            trafficPredicates.add(criteriaBuilder.like(root.get(key), likeValue));
                        }
                        break;

                    case "electronicDevice":
                        for (String e : (List<String>)value) {
                            String likeValue = "%"+e+"%";
                            electronicDevicePredicates.add(criteriaBuilder.like(root.get(key), likeValue));
                        }
                        break;

                    case "welfare":
                        for (String e : (List<String>)value) {
                            String likeValue = "%"+e+"%";
                            welfarePredicates.add(criteriaBuilder.like(root.get(key), likeValue));
                        }
                        break;

                    case "convenient":
                        for (String e : (List<String>)value) {
                            String likeValue = "%"+e+"%";
                            convenientPredicates.add(criteriaBuilder.like(root.get(key), likeValue));
                        }
                        break;


                }
            });
            Predicate roomSizeFinalPredicate = null;
            Predicate structureFinalPredicate = null;
            Predicate paymentFinalPredicate = null;
            Predicate trafficFinalPredicate = null;
            Predicate electronicDeviceFinalPredicate = null;
            Predicate welfareFinalPredicate = null;
            Predicate convenientFinalPredicate = null;

            List<Predicate> finalPredicateANDList = new ArrayList<>();

            if(!roomSizePredicates.isEmpty()){
                roomSizeFinalPredicate = criteriaBuilder.or(roomSizePredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(roomSizeFinalPredicate);
            }

            if(!structurePredicates.isEmpty()){
                structureFinalPredicate = criteriaBuilder.or(structurePredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(structureFinalPredicate);
            }

            if(!paymentPredicates.isEmpty()){
                paymentFinalPredicate = criteriaBuilder.or(paymentPredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(paymentFinalPredicate);
            }

            if(!trafficPredicates.isEmpty()){
                trafficFinalPredicate = criteriaBuilder.or(trafficPredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(trafficFinalPredicate);
            }

            if(!electronicDevicePredicates.isEmpty()){
                electronicDeviceFinalPredicate = criteriaBuilder.or(electronicDevicePredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(electronicDeviceFinalPredicate);
            }

            if(!welfarePredicates.isEmpty()){
                welfareFinalPredicate = criteriaBuilder.or(welfarePredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(welfareFinalPredicate);
            }

            if(!convenientPredicates.isEmpty()){
                convenientFinalPredicate = criteriaBuilder.or(convenientPredicates.toArray(new Predicate[0]));
                finalPredicateANDList.add(convenientFinalPredicate);
            }


            // (원룸 or 투룸 or 쓰리룸) AND (빌라 or 아파트 or 오피스텔) AND (..) AND (..)로 검색가능하게
            return criteriaBuilder.and(finalPredicateANDList.toArray(new Predicate[0]));

        };

    }

    /**
     * @author 미리
     * @param filter 검색어에 적용시킬 필드들과 검색어로 구성된 map
     * @return 검색어에 해당되는 리뷰들만 적용시키는 검색조건
     */
    public static Specification<Review> searchText(Map<String, Object> filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            filter.forEach((key, value) -> {
                String likeValue = "%" + value + "%";

                switch (key) {
                    case "postName": case "postContent": case "comment":
                    case "address": case "detailAddress": case "postCode": case "extraAddress":
                        predicates.add(criteriaBuilder.like(root.get(key).as(String.class), likeValue));
                        break;
                }
            });
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * @author 미리
     * @param tagName 검색할 태그명
     * @return 태그명으로 유사검색 후 태그에 적용시키는 검색조건
     */
    public static Specification<Tag> searchTagDetails(String tagName){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            String likeValue = "%" + tagName + "%";
            predicates.add(criteriaBuilder.like(root.get("tagName"), likeValue));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    /**
     * @author 미리
     * @param tags 적용시킬 태그들
     * @return 해당되는 태그들에 대해서만 리뷰에 적용시키는 검색조건
     */
    public static Specification<Review> searchTag(List<Tag> tags){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (Tag e : tags) {
                predicates.add(criteriaBuilder.isMember(tags, root.get("tags")));
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));

        };
    }



}
