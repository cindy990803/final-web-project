package com.project.bokduck.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.project.bokduck.domain.*;
import com.project.bokduck.repository.*;
import com.project.bokduck.service.CommunityService;
import com.project.bokduck.service.MainpageService;
import com.project.bokduck.service.MemberService;
import com.project.bokduck.service.PassEmailService;
import com.project.bokduck.specification.CommunitySpecs;
import com.project.bokduck.specification.ReviewSpecs;
import com.project.bokduck.util.CommunityFormVo;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.util.ReviewListVo;
import com.project.bokduck.validation.JoinFormValidator;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Order;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final MemberService memberService;
    private final PassEmailService passEmailService;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final PlatformTransactionManager transactionManager;
    private final CommunityService communityService;
    private final ImageRepository imageRepository;
    private final MainpageService mainpageService;
    private final PasswordEncoder passwordEncoder;
    private final CommentCommunityRepository commentCommunityRepository;

    /**
     * 임의의 리뷰글 및 커뮤니티글 생성
     */

    @PostConstruct
    @DependsOn("memberRepository")
    @Transactional
    public void createReviewsAndCommunities() {
        //리뷰글 생성
        TransactionTemplate tmpl = new TransactionTemplate(transactionManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {

                Long[] array = {1l, 2l};

                // 태그 만들어두기
                List<Tag> tagList = new ArrayList<>(); // 임시태그 담아보자
                String[] tagNameList = {"넓음", "깨끗함", "벌레없음"};

                for (int i = 0; i < tagNameList.length; ++i) {
                    Tag tag = new Tag();
                    tag.setTagName(tagNameList[i]);
                    tagList.add(tag);
                }

                tagRepository.saveAll(tagList);


                // 리뷰게시글을 만들어보자
                List<Review> reviewList = new ArrayList<>();
                ReviewCategory category = null;


                for (int i = 0; i < 50; ++i) {
                    category = new ReviewCategory();
                    if (i <= 24) {
                        category.setRoomSize(RoomSize.ONEROOM);
                        category.setStructure(Structure.VILLA);
                    } else {
                        category.setRoomSize(RoomSize.TWOROOM);
                        log.info("????");
                    }
                    category = reviewCategoryRepository.save(category);

                    Member member = memberRepository
                            .findById(array[(int) (Math.random() * array.length)]).orElseThrow();

                    Review review = Review.builder()
                            .postName((i + 1) + "번 게시물")
                            .postContent("어쩌구저쩌구")
                            .writer(member)
                            .comment("무난하다")
                            .regdate(LocalDateTime.now())
                            .updateDate(LocalDateTime.now())
                            .hit((int) (Math.random() * 10))
                            .star((int) (Math.random() * 5) + 1)
                            .likeCount((int) (Math.random() * 100))
                            .address("서울시 마포구 연희동 1-1")
                            .detailAddress("XX빌라")
                            .extraAddress("연희동")
                            .reviewStatus(i % 2 == 0 ? ReviewStatus.WAIT : ReviewStatus.COMPLETE)
//                            .reviewCategory(category)
                            .build();
                    review.setReviewCategory(reviewCategoryRepository.findById((long) (i + 6)).get());
                    reviewList.add(review);

                }
                reviewRepository.saveAll(reviewList);

                // 태그 포스트에 넣기
                List<Tag> tag1 = tagRepository.findAll();
                List<Post> tagPostList = postRepository.findAll();
                for (Tag t : tag1) {
                    t.setTagToPost(tagPostList);
                }

                // 포토리뷰 만들어두기
                List<Image> imageList = new ArrayList<>();
                String[] imageNameList = {"photo_1.jpg", "photo_2.jpg"};
                String[] imagePathList = {"/images/photo_1.jpg", "/images/photo_2.jpg"};

                for (int i = 0; i < 2; ++i) {
                    Image image = new Image();
                    image.setImageName(imageNameList[i]);
                    image.setImagePath(imagePathList[i]);
                    imageList.add(image);
                }

                imageRepository.saveAll(imageList);

                // 포토리뷰 포스트에 넣기
                List<Image> image1 = imageRepository.findAll();
                Post post = postRepository.findById(105l).orElseThrow();
                for (Image i : image1) {
                    i.setImageToPost(post);
                }

                // 멤버 like 만들기
                Member member = memberRepository.findById(1l).orElseThrow();
                List<Post> likePostList = new ArrayList<>();
                Post post1 = postRepository.findById(103l).orElseThrow();
                likePostList.add(post1);
                member.setLikes(likePostList);

                member = memberRepository.findById(2l).orElseThrow();
                likePostList = postRepository.findAll();
                member.setLikes(likePostList);
            }
        });

        //커뮤니티글 생성
        TransactionTemplate tmpl2 = new TransactionTemplate(transactionManager);
        tmpl2.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                Long[] arry = {1L, 2L};
                CommunityCategory[] categories = {CommunityCategory.TIP, CommunityCategory.EAT
                        , CommunityCategory.INTERIOR, CommunityCategory.BOARD};
                List<Community> communityList = new ArrayList<>();
                LocalDateTime localDateTime = LocalDateTime.now();

                List<Tag> tagList = new ArrayList<>(); // 임시태그 담아보자
                String[] tagNameList = {"태그1", "태그2", "태그3"};

                for (int i = 0; i < tagNameList.length; ++i) {
                    Tag tag = new Tag();
                    tag.setTagName(tagNameList[i]);
                    tagList.add(tag);
                }
                tagRepository.saveAll(tagList);


                for (int i = 0; i < 300; i++) {
                    Member member = memberRepository.findById(arry[(int) (Math.random() * arry.length)]).orElseThrow();
                    communityList.add(Community.builder()
                            .postName(i + "번 제목입니다.")
                            .postContent(i + "번 내용입니다.")
                            .writer(member)
                            .hit((int) ((Math.random() * 50) + 1))
                            .likeCount((int) (Math.random() * 100))
                            .regdate(localDateTime)
                            .updateDate(localDateTime)
                            .communityCategory(categories[(int) (Math.random() * categories.length)])
                            .build());
                }

                communityRepository.saveAll(communityList);

                List<Tag> tag2 = tagRepository.findAll();
                List<Post> tagPostList2 = postRepository.findAll();
                for (Tag t : tag2) {
                    t.setTagToPost(tagPostList2);
                }


                //커뮤니티 댓글 만들어놓기
                for (Community commu : communityList) {
                    Member member1 = memberRepository.findById(arry[(int) (Math.random() * arry.length)]).orElseThrow();
                    Member member2 = memberRepository.findById(arry[(int) (Math.random() * arry.length)]).orElseThrow();

                    CommentCommunity comment1 = CommentCommunity.builder()
                            .text("댓글1 본문입니다... 리뷰 잘 봤습니다.")
                            .nickname(member1.getNickname())
                            .nicknameOpen(member1.isNicknameOpen())
                            .community(commu)
                            .parentId(-1l)
                            .regdate(LocalDateTime.now())
                            .build();

                    CommentCommunity comment2 = CommentCommunity.builder()
                            .text("댓글2 본문입니다... 좋은 리뷰였습니다.")
                            .nickname(member2.getNickname())
                            .nicknameOpen(member2.isNicknameOpen())
                            .community(commu)
                            .parentId(-1l)
                            .regdate(LocalDateTime.now())
                            .build();

                    comment1 = commentCommunityRepository.save(comment1);
                    commentCommunityRepository.save(comment2);

                    CommentCommunity comment3 = CommentCommunity.builder()
                            .text("댓글1의 대댓글1입니다... 저도 그렇게 생각합니다.")
                            .nickname(member1.getNickname())
                            .nicknameOpen(member1.isNicknameOpen())
                            .community(commu)
                            .parentId(comment1.getId())
                            .regdate(LocalDateTime.now())
                            .build();

                    CommentCommunity comment4 = CommentCommunity.builder()
                            .text("댓글1의 대댓글2입니다... 하하하.")
                            .nickname(member2.getNickname())
                            .nicknameOpen(member2.isNicknameOpen())
                            .community(commu)
                            .parentId(comment1.getId())
                            .regdate(LocalDateTime.now())
                            .build();

                    commentCommunityRepository.save(comment3);
                    commentCommunityRepository.save(comment4);
                }
            }
        });
    }


    @InitBinder("joinFormVo")
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(new JoinFormValidator(memberRepository));
    }

    @RequestMapping("/")
    public String index(Model model) {

        //리뷰 인기게시글 불러오기
        Page<Review> reviewList1 = mainpageService.getReviewList(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList1", reviewList1);
        Page<Review> reviewList2 = mainpageService.getReviewList(PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList2", reviewList2);
        Page<Review> reviewList3 = mainpageService.getReviewList(PageRequest.of(2, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList3", reviewList3);

        //커뮤니티 인기게시글(좋아요순) 불러오기
        Page<Community> communityList = mainpageService.getCommunityList(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("communityList", communityList);

        //자취방꿀팁(일단 좋아요순으로 통일함) 불러오기
        Page<Community> communityTipList = mainpageService.getCommunityTipList(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("communityTipList", communityTipList);

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("joinFormVo", new JoinFormVo());
        return "/member/signup";
    }


    @PostMapping("/signup")
    public String signupSubmit(@Valid JoinFormVo joinFormVo, Errors errors) {
        log.info("joinFormVo : {}", joinFormVo);
        if (errors.hasErrors()) {
            log.info("회원가입 에러 : {}", errors.getAllErrors());
            return "/member/signup";
        }

        log.info("회원가입 정상!");

        memberService.processNewMember(joinFormVo);

        return "redirect:/";
    }

    @Transactional
    @GetMapping("/email-check")
    public String emailCheck(String username, String token, Model model) {

        Optional<Member> optional = memberRepository.findByUsername(username);
        boolean result;

        if (optional.isEmpty()) {
            result = false;
        } else if (!optional.get().getEmailCheckToken().equals(token)) {
            result = false;
        } else {
            result = true;
            optional.get().setEmailVerified(true);
        }

        String nickname = memberRepository.findByUsername(username).get().getNickname();

        model.addAttribute("nickname", nickname);
        model.addAttribute("result", result);

        return "member/email-check-result";
    }

    @GetMapping("/password")
    public String password() {
        return "member/password";
    }

    @PostMapping("/password")
    public String passwordSubmit(String username, Model model) {
        String message = "아이디 : ";
        Optional<Member> optional = memberRepository.findByUsername(username);
        if (optional.isEmpty()) {
            message = "아이디가 없습니다. 다시 한번 시도하세요.";

        } else {
            message = "임시 비밀번호가 발송되었습니다. 이메일을 확인해주세요.";
            passEmailService.sendPassEmail(optional.orElseThrow());
        }
        model.addAttribute("message", message);
        return "member/password";
    }

    @GetMapping("/community/write")
    public String communityWriteForm(Model model) {
        model.addAttribute("vo", new CommunityFormVo());
        return "post/community/write";
    }

    @PostMapping("/community/write")
    @Transactional
    public String communityWriteSubmit(@CurrentMember Member member, CommunityFormVo vo, Model model, @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        String strTags = vo.getTags();
        log.info("string형 태그들 : " + strTags);

        //DB에 저장할 List<Tag>형 변수 설정
        List<Tag> tagList = new ArrayList<>();

        if (!vo.getTags().isEmpty()) {
            JsonArray tagsJsonArray = new Gson().fromJson(vo.getTags(), JsonArray.class);

            for (int i = 0; i < tagsJsonArray.size(); ++i) {
                JsonObject object = tagsJsonArray.get(i).getAsJsonObject();
                String tagValue = object.get("value").getAsString();

                Tag tag = Tag.builder()
                        .tagName(tagValue)
                        .build();

                tagList.add(tag);
            }
        }


        //DB에 저장할 CommunityCategory형 변수 설정
        CommunityCategory category = CommunityCategory.TIP;

        switch (vo.getCommunityCategory()) {
            case 0:
                category = CommunityCategory.TIP;
                break;
            case 1:
                category = CommunityCategory.INTERIOR;
                break;
            case 2:
                category = CommunityCategory.EAT;
                break;
            case 3:
                category = CommunityCategory.BOARD;
                break;
            default:
        }


        //데이터를 DB에 저장
        Community community = Community.builder()
                .postName(vo.getPostName())
                .postContent(vo.getPostContent())
                .regdate(LocalDateTime.now())
                .writer(member)
                .tags(tagList)
                .communityCategory(category)
                .build();

        Community savedCommu = communityRepository.save(community);

        //TAG_TAG_TO_POST 테이블에 데이터 넣기
        for (Tag t : tagList) {
            if (t.getTagToPost() == null) {
                t.setTagToPost(new ArrayList<Post>());
            }
            t.getTagToPost().add(community);
        }

        return "redirect:/";  //TODO id값 어떻게 불러오지?("post/community/read?id=" + "?")
    }


    @GetMapping("/community/list")
    public String community(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                            @CurrentMember Member member, Model model,
                            @RequestParam(required = false, defaultValue = "all") String check) {
        Page<Community> communityList = communityService.findPage(pageable);

        if (member != null) {
            member = memberRepository.getById(member.getId());
            model.addAttribute("member", member);
        }
        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 5);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("check",check);
        model.addAttribute("communityList", communityList);
        model.addAttribute("state", "all");
        return "post/community/list";
    }

    @GetMapping("/community/list/category") //커뮤니티 카테고리, 페이지, 검색
    public String communityList(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model,
                                @RequestParam(required = false, defaultValue = "all") String community,
                                @RequestParam(required = false, defaultValue = "all") String check,
                                @CurrentMember Member member, String searchText) {

        String state = "all";
        String arrayLike =null;
        communityService.createLikeCount();
        Page<Community> communityList = null;
        if (check.equals("like")){
            check = "like";
        }else{
            check ="good";
        }

        switch (community) {
            case "all":
                communityList = communityRepository.findAll(pageable);
                state = "all";
                break;
            case "tip":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.TIP, pageable);
                state = "tip";
                break;
            case "interior":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.INTERIOR, pageable);
                state = "interior";
                break;
            case "eat":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.EAT, pageable);
                state = "eat";
                break;
            case "board":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.BOARD, pageable);
                state = "board";
                break;
        }

        if (member != null) {
            member = memberRepository.getById(member.getId());
            model.addAttribute("member", member);
        }

        if (searchText != null) { //검색 했을때
            state = "search";
            String[] search = {"postName", "postContent"};
            Specification<Community> searchSpec = null;

            //제목 내용 검색
            for (String s : search) {
                Map<String, Object> searchMap = new HashMap<>();
                searchMap.put(s, searchText);
                searchSpec =
                        searchSpec == null ? CommunitySpecs.searchText(searchMap)
                                : searchSpec.or(CommunitySpecs.searchText(searchMap));
            }

            //태그 검색
            Specification<Tag> tagSpec = CommunitySpecs.searchTagDetails(searchText);
            List<Tag> tagList = tagRepository.findAll(tagSpec);
            searchSpec = searchSpec.or(CommunitySpecs.searchTag(tagList));
            communityList = communityRepository.findAll(searchSpec, pageable);
        }


        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 5);
        model.addAttribute("pageable",pageable);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("state", state);
        model.addAttribute("searchText", searchText);
        model.addAttribute("arrayLike",arrayLike);
        model.addAttribute("communityList", communityList);
        model.addAttribute("member", member);
        model.addAttribute("check",check);

        return "post/community/list";
    }

    @GetMapping("/community/list/like") //카테고리 리스트 좋아요
    @ResponseBody
    public String like(Long id, @CurrentMember Member member) {
        String resultCode = "";
        String message = "";

        int likeCheck = communityService.findCommunityId(id).getLikers().size();// 좋아요 개수

        switch (communityService.addLike(member, id)) {
            case ERROR_AUTH:
                resultCode = "error.auth";
                message = "로그인이 필요한 서비스 입니다.";
                break;
            case ERROR_INVALID:
                resultCode = "error.invalid";
                message = "없는 게시글 입니다.";
                break;
            case ERROR_DUPLICATE:
                resultCode = "error.duplicate";
                message = "좋아요를 삭제하였습니다.";
                likeCheck -= 1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요를 추가하였습니다.";
                likeCheck += 1;
                break;
        }

        //gson의존성으로 수정
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        return jsonObject.toString();

    }


    @GetMapping("/community/read")
    @Transactional
    public String getCommunityRead(Model model, long id, @CurrentMember Member member) {

        Community community = communityRepository.findById(id).orElseThrow();

        //조회수 올리기
        if (! community.getVisitedMember().contains(memberRepository.getById(member.getId()))) { //아직 조회 안했으면
            community.setHit(community.getHit()+1);
            List members = community.getVisitedMember();
            members.add(member);
            community.setVisitedMember(members);
        }


        model.addAttribute("community",community);

        model.addAttribute("currMem",member);

        CommentCommunity comment = new CommentCommunity();
        model.addAttribute("comment", comment);

        CommentCommunity subComment = new CommentCommunity();
        model.addAttribute("subComment", subComment);

        return "post/community/read";
    }

//    @GetMapping("/community/delete")
//    public String communityDelete(@PageableDefault(size = 10,sort = "id", direction = Sort.Direction.DESC) Pageable pageable, @CurrentMember Member member, Model model, long id) {
//
//        communityRepository.deleteById(id);
//
//        return community(pageable, member, model);  //PostConstruct로 생성한 글을 지우려고 하면 모든 글이 삭제됨. 왜?
//    }

    @GetMapping("/community/delete")
    @ResponseBody
    public String communityDelete(long id) {

        communityRepository.deleteById(id);

        JsonObject jsonObject = new JsonObject();
        return jsonObject.toString();
    }

    @GetMapping("/community/modify")
    @Transactional
    public String getCommunityModify(Model model, long id) {

        Community community = communityRepository.findById(id).orElseThrow();

        CommunityFormVo vo = new CommunityFormVo();
        vo.setPostName(community.getPostName());
        vo.setPostContent(community.getPostContent());

        //태그 작업
        //[{"value":"태그"},{"value":"테스트"},{"value":"test"}]

        String strTags = "[";
        for (int i=0; i<community.getTags().size(); ++i) {
            strTags += "{\"value\":\"" + community.getTags().get(i).getTagName() + "\"}";

            if (i == community.getTags().size() - 1) break;

            strTags += ",";
        }
        strTags += "]";

        log.info("strTags : " + strTags);

        vo.setTags(strTags);

        //카테고리 작업
        int intCa = 0;

        if (community.getCommunityCategory() == CommunityCategory.TIP) {
            intCa = 0;
        }
        if (community.getCommunityCategory() == CommunityCategory.INTERIOR) {
            intCa = 1;
        }
        if (community.getCommunityCategory() == CommunityCategory.EAT) {
            intCa = 2;
        }
        if (community.getCommunityCategory() == CommunityCategory.BOARD) {
            intCa = 3;
        }

        log.info("intCa : " + intCa);

        vo.setCommunityCategory(intCa);


        model.addAttribute("vo", vo);
        model.addAttribute("communityId", id);

        return "post/community/modify";
    }

    @PostMapping("/community/modify/{id}")
    @Transactional
    public String communityModifySubmit(@PathVariable long id, @CurrentMember Member member, CommunityFormVo vo, Model model) {

        //DB에 저장할 List<Tag>형 변수 설정
        List<Tag> tagList = new ArrayList<>();

        if (!vo.getTags().isEmpty()) {
            JsonArray tagsJsonArray = new Gson().fromJson(vo.getTags(), JsonArray.class);

            for (int i = 0; i < tagsJsonArray.size(); ++i) {
                JsonObject object = tagsJsonArray.get(i).getAsJsonObject();
                String tagValue = object.get("value").getAsString();

                Tag tag = Tag.builder()
                        .tagName(tagValue)
                        .build();

                tagList.add(tag);
            }
        }

        //DB에 저장할 CommunityCategory형 변수 설정
        CommunityCategory category = CommunityCategory.TIP;

        switch (vo.getCommunityCategory()) {
            case 0:
                category = CommunityCategory.TIP;
                break;
            case 1:
                category = CommunityCategory.INTERIOR;
                break;
            case 2:
                category = CommunityCategory.EAT;
                break;
            case 3:
                category = CommunityCategory.BOARD;
                break;
            default:
        }



        Community community = communityRepository.findById(id).orElseThrow();
        community.setPostName(vo.getPostName());
        community.setPostContent(vo.getPostContent());
        community.setCommunityCategory(category);


        //TODO TAG_TAG_TO_POST 테이블에 수정한 데이터 갱신하기
        List<Tag> previousTagList = community.getTags();
        community.setTags(tagList);

        //현재 게시물을 수정 시 지운 태그들의 tagToPost에서 제거
        for(Tag t : previousTagList){
            List<Post> postList = t.getTagToPost();
            postList.remove(communityRepository.findById(id).orElseThrow());
            t.setTagToPost(postList);
        }

        //수정 시 추가한 태그들의 tagToPost에 현재 게시물 추가
        for(Tag t : tagList){
            if (t.getTagToPost()==null) {
                t.setTagToPost(new ArrayList<Post>());
            }
            t.getTagToPost().add(community);
        }

        return getCommunityRead(model, id, member);
    }

    @PostMapping("/community/read/comment/{id}")
    public String submitComment(CommentCommunity comment, @PathVariable long id, Model model, @CurrentMember Member member) {

        //DB에 댓글정보 저장
        CommentCommunity commentCommunity = CommentCommunity.builder()
                .nickname(member.getNickname())
                .nicknameOpen(member.isNicknameOpen())
                .regdate(LocalDateTime.now())
                .text(comment.getText())
                .parentId(-1l)
                .community(communityRepository.findById(id).orElseThrow())
                .build();

        commentCommunityRepository.save(commentCommunity);

        return getCommunityRead(model, id, member);
    }

    @PostMapping("/community/read/subcomment/{id}")
    public String submitSubComment(CommentCommunity subComment, @PathVariable long id, Model model, @CurrentMember Member member) {

        //DB에 답글정보 저장
        CommentCommunity subCommentCommunity = CommentCommunity.builder()
                .nickname(member.getNickname())
                .nicknameOpen(member.isNicknameOpen())
                .regdate(LocalDateTime.now())
                .text(subComment.getText())
                .parentId(subComment.getParentId())
                .community(communityRepository.findById(id).orElseThrow())
                .build();

        commentCommunityRepository.save(subCommentCommunity);

        return getCommunityRead(model, id, member);
    }

    @GetMapping("/community/read/like")
    @ResponseBody
    public String communitReadLike(Long id, @CurrentMember Member member) {
        // 좋아요 눌렀을 때
        log.info("좋아요 아이디 : {}", id);

        String resultCode = "";
        String message = "";

        // 좋아요 개수
        int likeCheck = communityRepository.findById(id).orElseThrow().getLikers().size();


        switch (communityService.addLike(member, id)) {
            case ERROR_AUTH:
                resultCode = "error.auth";
                message = "로그인이 필요한 서비스입니다.";
                break;
            case ERROR_INVALID:
                resultCode = "error.invalid";
                message = "삭제된 게시물 입니다.";
                break;
            case ERROR_DUPLICATE:
                resultCode = "duplicate";
                message = "좋아요 취소 완료!";
                likeCheck -= 1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요 완료!";
                likeCheck += 1;
                break;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        log.info("jsonObject.toString() : {}", jsonObject.toString());

        return jsonObject.toString();
    }

    @PostMapping("/community/read/comment")
    public String writeCommunityComment() {


        return "";
    }


    @GetMapping("/mypage")
    public String mypage(Model model,
                         @CurrentMember Member member) {
List<Review> reviewList = new ArrayList<>();
Review review;

        review = reviewRepository.getById(member.getId());



        return "/member/mypage";
    }





}
