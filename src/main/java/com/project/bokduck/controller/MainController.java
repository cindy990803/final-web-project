package com.project.bokduck.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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

import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.Review;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.service.MemberService;
import com.project.bokduck.service.PassEmailService;
import com.project.bokduck.service.ReviewService;
import com.project.bokduck.service.SmsService;

import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.validation.JoinFormValidator;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
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
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {


    private final MemberService memberService;
    private final SmsService smsService;
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final PassEmailService passEmailService;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final PlatformTransactionManager transactionManager;
    private final CommunityService communityService;
    private final ImageRepository imageRepository;
    private final MainpageService mainpageService;

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

                Long[] array = {1l,2l};

                // 임시 이미지 만들어주기
                List<Image> imageList = new ArrayList<>();
                String[] imgUrlList = {"https://postfiles.pstatic.net/MjAyMDEyMTNfMjky/MDAxNjA3ODYwOTk5Mzc0.aCwwUIuc05kh6ceHxTPfmNf6lKYvr6faPrQChc0XUOgg.uw9cTnUBkJz9RVrKQzB7nXWU8DOTJjciJmc7eXwMwjYg.JPEG.yujoo215/1607860353211.jpg?type=w773",
                        "https://postfiles.pstatic.net/MjAyMDEyMTNfNDUg/MDAxNjA3ODYwOTEzMDE2.Sn84rrRG4762s_wId0qZPpwOnEwBkVcAh5TKuELUyukg.-xDyC8Aeji3gjaK4lhtn_zTXW5n6YuXogQAR0-2t69cg.JPEG.yujoo215/1607860394134.jpg?type=w773",
                        "https://postfiles.pstatic.net/MjAyMDEyMTNfMjU0/MDAxNjA3ODYwOTM4NTI0.ThtknKgJrsQQNcWoiukB6CnirvlO2kxr2ZwrYzSpcjkg.ox-6mVkUI9Pm7YmgWeECci4ZOqOQ6TaSe-0d5dy9ddAg.JPEG.yujoo215/1607860392362.jpg?type=w773"};

                for(int i = 0; i < imgUrlList.length; ++i) {
                    Image image = new Image();
                    image.setImagePath(imgUrlList[i]);
                    imageList.add(image);
                }
                imageRepository.saveAll(imageList);


                // 태그 만들어두기
                List<Tag> tagList = new ArrayList<>(); // 임시태그 담아보자
                String[] tagNameList = {"넓음", "깨끗함", "벌레없음"};

                for(int i = 0; i < tagNameList.length; ++i){
                    Tag tag = new Tag();
                    tag.setTagName(tagNameList[i]);
                    tagList.add(tag);
                }

                tagRepository.saveAll(tagList);


                // 리뷰게시글을 만들어보자
                List<Review> reviewList = new ArrayList<>();
                ReviewCategory category = null;


                for(int i = 0; i < 50; ++i){
                    category = new ReviewCategory();
                    if (i<=24){
                        category.setRoomSize(RoomSize.ONEROOM);
                        category.setStructure(Structure.VILLA);
                    }else {
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
                            .hit((int) (Math.random() * 10))
                            .star((int) (Math.random() * 5) + 1)
                            .likeCount((int) (Math.random() * 100))
                            .address("서울시 마포구 연희동 1-1")
                            .detailAddress("XX빌라")
                            .extraAddress("연희동")
                            .reviewStatus(i % 2 == 0 ? ReviewStatus.WAIT : ReviewStatus.COMPLETE)
//                            .reviewCategory(category)
                            .build();
//이미지
                    for(Image image  : imageList) {
                        image.setImageName(review);
                    }
                    review.setUploadImage(imageList);

                    review.setReviewCategory(reviewCategoryRepository.findById((long)(i + 6)).get());
                    reviewList.add(review);

                }
                reviewRepository.saveAll(reviewList);

                // 태그 포스트에 넣기
                List<Tag> tag1 = tagRepository.findAll();
                List<Post> tagPostList= postRepository.findAll();
                for(Tag t : tag1){
                    t.setTagToPost(tagPostList);
                }

                // 포토리뷰 만들어두기
                List<Image> imageList = new ArrayList<>();
                String[] imageNameList = {"photo_1.jpg", "photo_2.jpg"};
                String[] imagePathList = {"/images/photo_1.jpg", "/images/photo_2.jpg"};

                for(int i = 0; i < 2; ++i){
                    Image image = new Image();
                    image.setImageName(imageNameList[i]);
                    image.setImagePath(imagePathList[i]);
                    imageList.add(image);
                }

                imageRepository.saveAll(imageList);

                // 포토리뷰 포스트에 넣기
                List<Image> image1 = imageRepository.findAll();
                Post post = postRepository.findById(105l).orElseThrow();
                for(Image i : image1){
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
                            .communityCategory(categories[(int) (Math.random() * categories.length)])
                            .build());
                }
                communityRepository.saveAll(communityList);

                List<Tag> tag2 = tagRepository.findAll();
                List<Post> tagPostList2 = postRepository.findAll();
                for (Tag t : tag2) {
                    t.setTagToPost(tagPostList2);
                }
            }
        });
    }



    @InitBinder("joinFormVo")
    protected void initBinder(WebDataBinder dataBinder) {
        dataBinder.addValidators(new JoinFormValidator(memberRepository));
    }

    @RequestMapping("/")
<<<<<<< HEAD
    public String index(Model model, @CurrentMember Member member) {
        List<Review> reviewList = reviewService.getReviewList();
        model.addAttribute("reviewList", reviewList);
        log.info("reviewList : {}", reviewList);
=======
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

>>>>>>> f3d124591d5b3eb9d9a6dc9681f4da2ac26cef2a
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
    @GetMapping("/id-search")
    public String idCheck() {

        return "member/id-search";
    }

    @PostMapping("/id/search")
    @ResponseBody
    public String sendSms(@RequestParam String tel, HttpServletRequest request) {
        // 인증번호 발송하고
        // "인증번호가 발송되었습니다." 를 response
        Member member = memberRepository.findByTel(tel).get();
        String message;
        if(member == null){
            message = "미등록..";
        }
        else {
            Random rand = new Random();
            String cerNum = "";
            for (int i = 0; i < 6; i++) {
                if (i == 0) {
                    String ran = Integer.toString(rand.nextInt(9) + 1);
                    cerNum += ran;
                    continue;
                }
                String ran = Integer.toString(rand.nextInt(10));
                cerNum += ran;
            }

            System.out.println("수신자 번호 : " + tel);
            System.out.println("인증번호 : " + cerNum);
            smsService.certifiedPhoneNumber(tel, cerNum);

            // 인증번호를 세션객체에 담는다.
            request.getSession().setAttribute("cerNum", cerNum);

            message = "성공.....";
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", message);
        return jsonObject.toString();
    }

    @PostMapping("/id-search-result")
    public String checkSms(@RequestParam String name, @RequestParam String tel,
                           @RequestParam String num, HttpServletRequest request, Model model){
        // 진짜 인증번호와 num 파라미터 비교
        String message;
        String realCerNum =(String)request.getSession().getAttribute("cerNum");

        // 맞으면 ==>
        if(num.equals(realCerNum)) {

            //    가입된 연락처면  ==> model.addAttribute("message", "aaa@a.a") 로 회원 이메일 담음
           if(memberService.containsTel(tel)) {
               message = memberService.getEmail(tel);
           } else {
               message = "복덕복덕에 가입한 번호가 아닙니다.";
           }
        } else {

            // 틀리면 ==>model.addAttribute("message", "인증번호가 잘못되었습니다.") 로 회원 이메일 담음
            message = "인증번호가 잘못 되었습니다.";
            model.addAttribute("message", message);
        }


        //    가입된 연락처면  ==> model.addAttribute("message", "aaa@a.a") 로 회원 이메일 담음
        // 틀리면 ==>model.addAttribute("message", "인증번호가 잘못되었습니다.") 로 회원 이메일 담음
        return "member/id-search-result";
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
    public String communityWriteSubmit(@CurrentMember Member member, CommunityFormVo vo, Model model) {

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

        communityRepository.save(community);

        //TAG_TAG_TO_POST 테이블에 데이터 넣기
        for(Tag t : tagList){
            if (t.getTagToPost()==null) {
                t.setTagToPost(new ArrayList<Post>());
            }
            t.getTagToPost().add(community);
        }

        return "index";  //TODO 커뮤니티글 보기 기능 완성 후 "post/community/read"로 바꾸기
    }
// 리뷰 컨트롤러

    @GetMapping("/review/read")
    public String read(Model model,@RequestParam(name = "reviewId") Long id, @CurrentMember Member member){
        Review review = reviewService.getReview(id);

        model.addAttribute("review",review);
        model.addAttribute("currentMember", member);

        return "post/review/read";
    }



    @GetMapping("/community/list")
    public String community( @PageableDefault(size = 10,sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                             @CurrentMember Member member, Model model) {

//        pageable = PageRequest.of(0,10, Sort.by(Sort.Direction.DESC,"likers"));
        Page<Community> communityList = communityService.findPage(pageable);

        if (member!=null){
            member = memberRepository.getById(member.getId());
            model.addAttribute("member", member);
        }


        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        String state="all";
        model.addAttribute("state",state);
        model.addAttribute("communityList", communityList);
        model.addAttribute("searchText"," ");
        return "post/community/list";
    }

    @GetMapping("/community/list/{value}") //커뮤니티 카테고리
    public String communityList(@PageableDefault(page =0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model,@PathVariable String value, @CurrentMember Member member) {

        String state="all";
        Page<Community> communityList = null;
        switch (value) {
            case "all":
                communityList = communityService.findPage(pageable);
                state = "all";
                model.addAttribute("state",state);
                model.addAttribute("communityList", communityList);
                break;
            case "tip":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.TIP,pageable);
                state = "tip";
                model.addAttribute("state",state);
                model.addAttribute("communityList", communityList);
                break;
            case "interior":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.INTERIOR,pageable);
                state = "interior";
                model.addAttribute("state",state);
                model.addAttribute("communityList", communityList);
                break;
            case "eat":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.EAT,pageable);
                state = "eat";
                model.addAttribute("state",state);
                model.addAttribute("communityList", communityList);
                break;
            case "board":
                communityList = communityService.findCommunityCategoryPage(CommunityCategory.BOARD,pageable);
                state = "board";
                model.addAttribute("state",state);
                model.addAttribute("communityList", communityList);
                break;
        }

        if (member !=null){
            member = memberRepository.getById(member.getId());
            model.addAttribute("member", member);
        }

        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 4);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        model.addAttribute("member",member);
        return "post/community/list";
    }



    @GetMapping("/community/list/like") //카테고리 리스트 좋아요
    @ResponseBody
    public String like(Long id, @CurrentMember Member member, Model model){
        String resultCode="";
        String message="";

        int likeCheck=communityService.findCommunityId(id).getLikers().size();// 좋아요 개수

        switch (communityService.addLike(member,id)){
            case ERROR_AUTH:
                resultCode="error.auth";
                message = "로그인이 필요한 서비스 입니다.";
                break;
            case ERROR_INVALID:
                resultCode = "error.invalid";
                message = "없는 게시글 입니다.";
                break;
            case ERROR_DUPLICATE:
                resultCode = "error.duplicate";
                message = "좋아요를 삭제하였습니다.";
                likeCheck-=1;
                break;
            case OK:
                resultCode = "ok";
                message = "좋아요를 추가하였습니다.";
                likeCheck +=1;
                break;
        }

        //gson의존성으로 수정
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode",resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        return jsonObject.toString();

    }


    @GetMapping("/community/search") //검색 결과 나오는 것
    public String communitySearch(String searchText, Model model
            ,@PageableDefault(page =0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        Page<Community> communityList =null;
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

        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 4);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 4);

        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("communityList",communityList);
        model.addAttribute("state", "search");
        model.addAttribute("searchText",searchText);

        return "post/community/list";
    }

}
