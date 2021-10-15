package com.project.bokduck.service;

import com.project.bokduck.domain.Community;
import com.project.bokduck.domain.CommunityCategory;
import com.project.bokduck.domain.Review;
import com.project.bokduck.repository.CommunityRepository;
import com.project.bokduck.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 메인페이지 서비스
 * @author 이선주
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MainpageService {


    private final CommunityRepository communityRepository;
    private final ReviewRepository reviewRepository;

    public Page<Review> getReviewList(Pageable pageable) {
        return reviewRepository.findAll(pageable);
    }

    public Page<Community> getCommunityList(Pageable pageable) {
        return communityRepository.findAll(pageable);
    }

    public Page<Community> getCommunityTipList(Pageable pageable) {
        return communityRepository.findByCommunityCategory(CommunityCategory.TIP, pageable);
    }
}
