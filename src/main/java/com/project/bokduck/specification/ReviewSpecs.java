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

    public static Specification<Image> searchPhotoReviewDetails(List<String> imageName){
        return (root, query, criteriaBuilder) -> {

                List<Predicate> predicates = new ArrayList<>();
                for (String e : imageName) {
                    predicates.add(criteriaBuilder.equal(root.get("imageName"), e));
                }
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Review> searchPhotoReview(){
        return (root, query, criteriaBuilder) -> {

                List<Predicate> predicates = new ArrayList<>();
                    predicates.add(criteriaBuilder.isNotEmpty(root.get("uploadImage")));
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Review> searchReviewStatus(ReviewStatus reviewStatus){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("reviewStatus"), reviewStatus));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    public static Specification<Review> searchCategory(List<ReviewCategory> category){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

                for (ReviewCategory e : category) {
                    predicates.add(criteriaBuilder.equal(root.get("reviewCategory"), e));
                }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ReviewCategory> searchCategoryDetails(Map<String, List<?>> filter) {
        // 빌라, 원룸, 투룸 ==> Map  : {"roomSize" : [ RoomSize.ONEROOM, RoomSize.TWOROOM ], "structure" : [ Structure.VILLA ] }
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
                            log.info("e : {} / roomSizePredicates : {} ", e, roomSizePredicates);
                        }

                        break;

                    case "structure":
                        for (Structure e : (List<Structure>)value) {
                            structurePredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / structurePredicates : {} ", e, structurePredicates);
                        }
                        break;

                    case "payment":
                        for (Payment e : (List<Payment>)value) {
                            paymentPredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / paymentPredicates : {} ", e, paymentPredicates);
                        }
                        break;

                    case "traffic":
                        for (Traffic e : (List<Traffic>)value) {
                            trafficPredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / traffictPredicates : {} ", e, trafficPredicates);
                        }
                        break;

                    case "electronicDevice":
                        for (ElectronicDevices e : (List<ElectronicDevices>)value) {
                            electronicDevicePredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / electronicDevicePredicates : {} ", e, electronicDevicePredicates);
                        }
                        break;

                    case "welfare":
                        for (Welfare e : (List<Welfare>)value) {
                            welfarePredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / welfarePredicates : {} ", e, welfarePredicates);
                        }
                        break;

                    case "convenient":
                        for (Convenient e : (List<Convenient>)value) {
                            convenientPredicates.add(criteriaBuilder.equal(root.get(key), e));
                            log.info("e : {} / convenientPredicates : {} ", e, convenientPredicates);
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


            // (원룸 or 투룸 or 쓰리룸) AND (빌라 or 아파트 or 오피스텔) AND (..) AND (..)
            return criteriaBuilder.and(finalPredicateANDList.toArray(new Predicate[0]));

        };

    }


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

    public static Specification<Tag> searchTagDetails(String tagName){
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            String likeValue = "%" + tagName + "%";
            predicates.add(criteriaBuilder.like(root.get("tagName"), likeValue));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

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
