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
import com.project.bokduck.util.CommunityFormVo;
import com.project.bokduck.domain.Member;
import com.project.bokduck.domain.Review;
import com.project.bokduck.repository.MemberRepository;
import com.project.bokduck.service.ReviewService;
import com.project.bokduck.util.CurrentMember;
import com.project.bokduck.validation.JoinFormValidator;
import com.project.bokduck.validation.JoinFormVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final PassEmailService passEmailService;
    private final TagRepository tagRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final ReviewCategoryRepository reviewCategoryRepository;
    private final PlatformTransactionManager transactionManager;
    private final CommunityService communityService;
    private final ImageRepository imageRepository;
    private final FileRepository fileRepository;
    private final MainpageService mainpageService;
    private final PasswordEncoder passwordEncoder;
    private final CommentCommunityRepository commentCommunityRepository;
    private final CommentReviewRepository commentReviewRepository;

    /**
     * 임의의 리뷰글 및 커뮤니티글 생성
     * @author miri
     * @author 미리, 이선주
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
                            .hit((int) (Math.random() * 10))
                            .star((int) (Math.random() * 5) + 1)
                            .address("서울특별시 마포구 월드컵로34길 14")
                            .detailAddress("XX빌라")
                            .extraAddress("연희동")
                            .reviewStatus(ReviewStatus.COMPLETE)
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

                //계약서 만들어두기
                List<File> fileList = new ArrayList<>();
                String[] fileNameList = {"floating1.png", "floating2.png"};
                String[] filePathList = {"/images/floating1.png", "/images/floating3.png"};

                for (int i = 0; i < 2; ++i) {
                    File file = new File();
                    file.setFilePath(filePathList[i]);
                    fileList.add(file);
                }

                fileRepository.saveAll(fileList);

                // 계약서 포스트에 넣기
                List<File> file1 = fileRepository.findAll();
                for (File f : file1) {
                    f.setFileToPost(post);
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

                //리뷰 댓글 만들어놓기
                for (Review commu : reviewList) {
                    Member member1 = memberRepository.findById(array[(int) (Math.random() * array.length)]).orElseThrow();
                    Member member2 = memberRepository.findById(array[(int) (Math.random() * array.length)]).orElseThrow();

                    CommentReview comment1 = CommentReview.builder()
                            .text("댓글1 본문입니다... 리뷰 잘 봤습니다.")
                            .nickname(member1.getNickname())
                            .nicknameOpen(member1.isNicknameOpen())
                            .review(commu)
                            .parentId(-1l)
                            .regdate(LocalDateTime.now())
                            .build();

                    CommentReview comment2 = CommentReview.builder()
                            .text("댓글2 본문입니다... 좋은 리뷰였습니다.")
                            .nickname(member2.getNickname())
                            .nicknameOpen(member2.isNicknameOpen())
                            .review(commu)
                            .parentId(-1l)
                            .regdate(LocalDateTime.now())
                            .build();

                    comment1 = commentReviewRepository.save(comment1);
                    commentReviewRepository.save(comment2);

                    CommentReview comment3 = CommentReview.builder()
                            .text("댓글1의 대댓글1입니다... 저도 그렇게 생각합니다.")
                            .nickname(member1.getNickname())
                            .nicknameOpen(member1.isNicknameOpen())
                            .review(commu)
                            .parentId(comment1.getId())
                            .regdate(LocalDateTime.now())
                            .build();

                    CommentReview comment4 = CommentReview.builder()
                            .text("댓글1의 대댓글2입니다... 하하하.")
                            .nickname(member2.getNickname())
                            .nicknameOpen(member2.isNicknameOpen())
                            .review(commu)
                            .parentId(comment1.getId())
                            .regdate(LocalDateTime.now())
                            .build();

                    commentReviewRepository.save(comment3);
                    commentReviewRepository.save(comment4);
                }
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

    /**
     * "/" 요청을 받으면 메인페이지를 불러온다
     * @author 이선주
     * @param model 리턴되는 페이지로 애트리뷰트 전달
     * @return index.html
     */
    @RequestMapping("/")
    public String index(Model model) {

        //리뷰 인기게시글 불러오기
        Page<Review> reviewList1 = mainpageService.getReviewList(PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList1", reviewList1);
        Page<Review> reviewList2 = mainpageService.getReviewList(PageRequest.of(1, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList2", reviewList2);
        Page<Review> reviewList3 = mainpageService.getReviewList(PageRequest.of(2, 3, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("reviewList3", reviewList3);

        //커뮤니티 인기게시글 불러오기
        Page<Community> communityList = mainpageService.getCommunityList(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "likeCount")));
        model.addAttribute("communityList", communityList);

        //자취방꿀팁 불러오기
        Page<Community> communityTipList = mainpageService.getCommunityTipList(PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("communityTipList", communityTipList);

        return "index";
    }


    /**
     * @author miri
     * @author 미리
     * @return 로그인 페이지
     */
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    /**
     * 회원가입 버튼이 눌리면(요청이 들어오면) 회원가입 폼 페이지를 불러온다
     * @author 이선주
     * @param model 리턴되는 페이지로 애트리뷰트 전달
     * @return /member/signup.html
     */
    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("joinFormVo", new JoinFormVo());
        return "/member/signup";
    }

    /**
     * 회원가입 폼을 입력 후 가입 버튼을 눌렀을 때, DB에 사용자가 입력한 정보들을 저장하고 강제 로그인해준다
     * @author 이선주
     * @param joinFormVo 회원가입 입력 폼을 위한 VO
     * @param errors 일어날 수 있는 회원가입 에러들
     * @return 메인페이지로 되돌아간다
     */
    @PostMapping("/signup")
    public String signupSubmit(@Valid JoinFormVo joinFormVo, Errors errors) {
        if (errors.hasErrors()) {
            log.info("회원가입 에러 : {}", errors.getAllErrors());
            return "/member/signup";
        }

        memberService.processNewMember(joinFormVo);

        return "redirect:/";
    }

    /**
     * 회원가입 완료 후 사용자가 입력한 이메일주소로 이메일을 보내 인증한다
     * @author 이선주
     * @param username 회원가입한 사용자의 이메일
     * @param token 이메일이 일치하는지 확인하기 위한 토큰
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @return member/email-check-result.html
     */
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

    /**
     * 비밀번호 찾기 버튼 누르면 비밀번호 찾기 페이지를 불러온다.
     * @author 민경
     * @return member/password.html
     */
    @GetMapping("/password")
    public String password() {
        return "member/password";
    }

    /**
     * DB상에 id를 확인하고 id 확인 후 일치하면 해당 id로 임시 비밀번호를 발송한다.
     * @author 민경
     * @param username db 에서 비교 하는 값
     * @param model
     * @return 성공  -> 메일 발송, 성공 메세지 출력 , 실패 - 실패 메세지 출력 으로 member/password 뷰 변경
     */
    @PostMapping("/password")
    public String passwordSubmit(String username, Model model) {
        String message = " ";
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
    /**
     * 리뷰 상세보기 리뷰글 연결
     * @author 원재
     * @param model
     * @param id 리뷰글 id
     * @param member 현재 로그인한 회원
     * @return post/review/read.html
     */
    @GetMapping("/review/read")
    public String readReview(Model model, @RequestParam(name = "id") Long id, @CurrentMember Member member) {
        Review review = reviewService.findById(id);

        reviewService.increaseHit(id);

        model.addAttribute("review", review);
        model.addAttribute("currentMember", member);

        CommentReview comment = new CommentReview();
        model.addAttribute("comment", comment);

        CommentReview subComment = new CommentReview();
        model.addAttribute("subComment", subComment);

        return "post/review/read";
    }

    /**
     * 리뷰글 좋아요 누를시 특정글 좋아요 증가
     * @author 원재
     * @param id 리뷰 id
     * @param member 현재 로그인한 회원
     * @return
     */
    // 리뷰 컨트롤러
    @PostMapping("/read/like")
    @ResponseBody
    public String readLikeReview(Long id, @CurrentMember Member member) {
        // 좋아요 눌렀을 때

        String resultCode = "";
        String message = "";

        // 좋아요 개수
        int likeCheck = reviewService.findById(id).getLikers().size();


        switch (reviewService.addLike(member, id)) {
            case ERROR_AUTH:
                resultCode = "error.auth";
                message = "로그인이 필요한 서비스입니다.";
                break;
            case ERROR_INVALID:
                resultCode = "error.invalid";
                message = "삭제된 게시물 입니다.";
                break;
            case DUPLICATE:
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

    /**
     * 리뷰글 작성자 본인일시 디비에 리뷰글 삭제
     * @author 원재
     * @param id
     * @return jsonObject
     */
    @GetMapping("/read/delete")
    @ResponseBody
    public String reviewDelete(Long id) {

        String resultCode = "";
        String message = "";
        if (reviewService.deleteById(id)) {

            resultCode = "200";
            message = "삭세 성공";
        } else {
            resultCode = "400";
            message = "실패 되었습니다.";
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);

        return jsonObject.toString();
    }

    /**
     * 리뷰 댓글 작성후 DB에 저장후 화면 전달
     * @author 원재
     * @param comment 댓글
     * @param id 리뷰 id
     * @param model
     * @param member 현재 로그인한 회원
     * @return 리뷰글
     */
    @PostMapping("/review/read/comment/{id}")
    public String reviewComment(CommentReview comment, @PathVariable long id, Model model, @CurrentMember Member member) {

        //DB에 댓글정보 저장
        CommentReview commentReview = CommentReview.builder()
                .nickname(member.getNickname())
                .nicknameOpen(member.isNicknameOpen())
                .regdate(LocalDateTime.now())
                .text(comment.getText())
                .parentId(-1l)
                .review(reviewRepository.findById(id).orElseThrow())
                .build();

        commentReviewRepository.save(commentReview);

        return readReview(model, id, member);
    }

    /**
     * 리뷰 댓글 대댓글일 경우 DB에 저장
     * @author 원재
     * @param subComment 대댓글
     * @param id 리뷰 id
     * @param model
     * @param member 현재 로그인한 회원
     * @return 리뷰 상세보기
     */
    @PostMapping("/review/read/subcomment/{id}")
    public String reviewSubComment(CommentReview subComment, @PathVariable long id, Model model, @CurrentMember Member member) {

        //DB에 답글정보 저장
        CommentReview subCommentCommunity = CommentReview.builder()
                .nickname(member.getNickname())
                .nicknameOpen(member.isNicknameOpen())
                .regdate(LocalDateTime.now())
                .text(subComment.getText())
                .parentId(subComment.getParentId())
                .review(reviewRepository.findById(id).orElseThrow())
                .build();

        commentReviewRepository.save(subCommentCommunity);

        return readReview(model, id, member);
    }

    /**
     * 커뮤니티 '글쓰기' 버튼을 눌렀을 때 글쓰기 폼 페이지를 불러온다
     * @author 이선주
     * @param model 리턴할 페이지로 애트리뷰트 전달
     *@return post/community/write.html
     */
    @GetMapping("/community/write")
    public String communityWriteForm(Model model) {
        model.addAttribute("vo", new CommunityFormVo());
        return "post/community/write";
    }

    /**
     * 글쓰기를 완료하고 '게시' 버튼을 눌렀을 때 DB에 사용자가 입력한 정보를 저장한다
     * @author 이선주
     * @param member 글쓴이
     * @param vo 커뮤니티 글 입력 폼을 위한 VO
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @return 글 상세보기 페이지로 이동
     */
    @PostMapping("/community/write")
    @Transactional
    public String communityWriteSubmit(@CurrentMember Member member, CommunityFormVo vo, Model model) {

        String strTags = vo.getTags();

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

        return getCommunityRead(model, savedCommu.getId(), member);
    }

    /**
     * 커뮤니티 탭을 클릭하면 커뮤니티 리스트 화면페이지를 불러온다.
     * @author 민경
     * @param pageable 한 페이지당 보여줄 개수
     * @param member 현재 로그인한 회원
     * @param model
     * @param check 정렬 기준
     * @return 커뮤니티 리스트 화면
     */
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
        model.addAttribute("check", check);
        model.addAttribute("communityList", communityList);
        model.addAttribute("state", "all");

        return "post/community/list";
    }

    /**
     * 커뮤니티 리스트 화면에서 카테고리, 정렬, 페이징, 검색 한 기준에 따라서 변경된 값으로 list 출력
     * @author 민경
     * @param pageable 한 페이지당 글 게수
     * @param model
     * @param community 카테고리 종류
     * @param check 최신순, 좋아요 순 정렬 기준
     * @param member 현재 로그인한 멤버
     * @param searchText 검색할 키워드
     * @return post/community/list.html
     */
    @GetMapping("/community/list/category")
    public String communityList(@PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                Model model,
                                @RequestParam(required = false, defaultValue = "all") String community,
                                @RequestParam(required = false, defaultValue = "all") String check,
                                @CurrentMember Member member, String searchText) {

        String state = "all";
        String arrayLike = null;
        communityService.createLikeCount();
        Page<Community> communityList = null;
        if (check.equals("like")) {
            check = "like";
        } else {
            check = "good";
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

        if (searchText != null) {
            if (!searchText.isEmpty()) {
                state = "search";
                String[] search = {"postName", "postContent"};
                Specification<Community> searchSpec = null;

                for (String s : search) {
                    Map<String, Object> searchMap = new HashMap<>();
                    searchMap.put(s, searchText);
                    searchSpec =
                            searchSpec == null ? CommunitySpecs.searchText(searchMap)
                                    : searchSpec.or(CommunitySpecs.searchText(searchMap));

                }


                Specification<Tag> tagSpec = CommunitySpecs.searchTagDetails(searchText);
                List<Tag> tagList = tagRepository.findAll(tagSpec);
                searchSpec = searchSpec.or(CommunitySpecs.searchTag(tagList));
                communityList = communityRepository.findAll(searchSpec, pageable);
            }
        }

        int startPage = Math.max(1, communityList.getPageable().getPageNumber() - 5);
        int endPage = Math.min(communityList.getTotalPages(), communityList.getPageable().getPageNumber() + 5);
        model.addAttribute("pageable", pageable);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("state", state);
        model.addAttribute("searchText", searchText);
        model.addAttribute("arrayLike", arrayLike);
        model.addAttribute("communityList", communityList);
        model.addAttribute("member", member);
        model.addAttribute("check", check);

        return "post/community/list";
    }

    /**
     * 커뮤니티 리스트에서서 '좋아요' 버튼 눌렀을때 실패시 메세지를 출력하고 성공시 하트의 사진이 변경, db 변경
     * author 민경
     * @param member 좋아요 누른 멤버
     * @return 실패 -> 메세지 출력, 성공 -> 하트의 사진 변경 + db에 좋아요 한 게시물이 생성
     */
    @GetMapping("/community/list/like") //카테고리 리스트 좋아요
    @ResponseBody
    public String like(Long id, @CurrentMember Member member) {
        String resultCode = "";
        String message = "";

        int likeCheck = communityService.findCommunityId(id).getLikers().size();

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

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("resultCode", resultCode);
        jsonObject.addProperty("message", message);
        jsonObject.addProperty("likeCheck", likeCheck);

        return jsonObject.toString();
    }

    /**
     * 비밀번호 변경 버튼을 클릭 했을때 보여주는 뷰
     * @author 민경
     * @param model
     * @param member 현재 로그인한 회원 정보
     * @return 성공 -> /.html 이동 실패 -> member/password-change.html
     */
    @RequestMapping("/password/change")
    public String passwordChange(Model model, @CurrentMember Member member) {
        if (member == null) {
            return "redirect:/";
        }
        model.addAttribute("member", "member");
        return "member/password-change";
    }

    /**
     * 커뮤니티 상세보기 페이지 요청이 들어왔을 때 페이지를 불러온다
     * @author 이선주
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @param id 상세보기할 글의 id
     * @param member 현재 로그인해있는 유저
     * @return 커뮤니티 상세보기 페이지로 이동
     */
    @GetMapping("/community/read")
    @Transactional
    public String getCommunityRead(Model model, long id, @CurrentMember Member member) {

        Community community = communityRepository.findById(id).orElseThrow();

        if (community.getVisitedMember() == null) {
            community.setVisitedMember(new ArrayList<>());
        }
        if (community.getLikers() == null) {
            community.setLikers(new ArrayList<>());
        }
        if (community.getCommentCommunity() == null) {
            community.setCommentCommunity(new ArrayList<>());
        }

        //조회수 올리기
        if (!community.getVisitedMember().contains(memberRepository.getById(member.getId()))) { //아직 조회 안했으면
            community.setHit(community.getHit() + 1);
            List members = community.getVisitedMember();
            members.add(member);
            community.setVisitedMember(members);
        }

        model.addAttribute("community", community);
        model.addAttribute("currMem", member);

        CommentCommunity comment = new CommentCommunity();
        model.addAttribute("comment", comment);

        CommentCommunity subComment = new CommentCommunity();
        model.addAttribute("subComment", subComment);

        return "post/community/read";
    }

    /**
     * '삭제' 버튼을 눌렀을 때 커뮤니티 게시글을 DB에서 삭제해준다
     * @author 이선주
     * @param id 삭제할 커뮤니티 글의 id
     * @return jsonObject
     */
    @GetMapping("/community/delete")
    @ResponseBody
    public String communityDelete(long id) {

        communityRepository.deleteById(id);

        JsonObject jsonObject = new JsonObject();

        return jsonObject.toString();
    }

    /**
     * 커뮤니티 게시글 수정 페이지 요청이 들어왔을 때 수정 폼 페이지를 불러온다
     * @author 이선주
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @param id 수정할 글의 id
     * @return 글 수정 페이지
     */
    @GetMapping("/community/modify")
    @Transactional
    public String getCommunityModify(Model model, long id) {

        Community community = communityRepository.findById(id).orElseThrow();

        CommunityFormVo vo = new CommunityFormVo();
        vo.setPostName(community.getPostName());
        vo.setPostContent(community.getPostContent());

        String strTags = "[";
        for (int i = 0; i < community.getTags().size(); ++i) {
            strTags += "{\"value\":\"" + community.getTags().get(i).getTagName() + "\"}";

            if (i == community.getTags().size() - 1) break;

            strTags += ",";
        }
        strTags += "]";

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

        vo.setCommunityCategory(intCa);

        model.addAttribute("vo", vo);
        model.addAttribute("communityId", id);

        return "post/community/modify";
    }

    /**
     * 사용자가 폼 작성 후 '수정'을 누르면 DB에 수정된 정보를 저장한다
     * @author 이선주
     * @param id 수정할 글의 id
     * @param member 글을 수정한 유저(현재 로그인해있는 유저)
     * @param vo 수정한 정보를 담을 커뮤니티 글 입력 폼 VO
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @return 수정된 글 상세보기 페이지
     */
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

        List<Tag> previousTagList = community.getTags();
        community.setTags(tagList);

        //현재 게시물을 수정 시 지운 태그들의 tagToPost에서 제거
        for (Tag t : previousTagList) {
            List<Post> postList = t.getTagToPost();
            postList.remove(communityRepository.findById(id).orElseThrow());
            t.setTagToPost(postList);
        }

        //수정 시 추가한 태그들의 tagToPost에 현재 게시물 추가
        for (Tag t : tagList) {
            if (t.getTagToPost() == null) {
                t.setTagToPost(new ArrayList<Post>());
            }
            t.getTagToPost().add(community);
        }

        return getCommunityRead(model, id, member);
    }

    /**
     * 댓글을 쓴 후 댓글쓰기 버튼을 누르면 댓글 정보를 DB에 저장한다
     * @author 이선주
     * @param comment 쓴 댓글 내용을 담는 객체
     * @param id 댓글을 쓴 글의 id
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @param member 댓글을 쓴 유저(현재 로그인해있는 유저)
     * @return 댓글 정보가 업데이트된 상세보기 페이지
     */
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

    /**
     * 답글을 쓴 후 답글쓰기 버튼을 누르면 답글 정보를 DB에 저장한다
     * @author 이선주
     * @param subComment 쓴 답글 내용을 담는 객체
     * @param id 댓글을 쓴 글의 id
     * @param model 리턴할 페이지로 애트리뷰트 전달
     * @param member 답글을 쓴 유저(현재 로그인해있는 유저)
     * @return 답글 정보가 업데이트된 상세보기 페이지
     */
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

    /**
     * 좋아요를 누르면 DB의 정보를 업데이트한다
     * @author 이선주
     * @param id 좋아요를 누를 글의 id
     * @param member 좋아요를 누를 유저(현재 로그인해있는 유저)
     * @return jsonObject
     */
    @GetMapping("/community/read/like")
    @ResponseBody
    public String communitReadLike(Long id, @CurrentMember Member member) {
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

        return jsonObject.toString();
    }

    /**
     * 마이페이지를 클릭하면 마이페이지 이동
     * @author 민경,  MunKyoung
     * @param model
     * @param member 현재 로그인한 회원
     * @param pageable
     * @param check
     * @param page
     * @return /member/mypage.html
     */
    @GetMapping("/mypage")
    public String mypage(Model model,
                         @CurrentMember Member member,
                         @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam(required = false, defaultValue = "all") String check,
                         @RequestParam(value = "page", defaultValue = "0") int page
    ) {
        Review review;

        if (member == null) {
            return "redirect:/";
        } else {
            member = memberRepository.findById(member.getId()).orElseThrow();
        }
        review = reviewRepository.getById(member.getId());
        model.addAttribute("member", member);
        Page<Community> communityList = communityRepository.findAllByWriter(member, pageable);
        Page<Review> reviewList = reviewRepository.findAllByWriter(member, pageable);
        model.addAttribute("communityMaxPage", communityList.getSize());
        model.addAttribute("reviewMaxPage", reviewList.getSize());
        model.addAttribute("check", check);
        model.addAttribute("state", "all");
        model.addAttribute("communityList", communityList);
        model.addAttribute("reviewList", reviewList);
        return "/member/mypage";
    }

    /** 현재 비밀번호, 새로운 비밀번호를 입력받아 비밀번호 변경한다.
     * @author 민경
     * @param member 현재 로그인한 회원
     * @param oldpass 현재 비밀번호
     * @param pass 새로운 비밀번호
     * @param repass 새로운 비밀번호 확인
     * @return 실패 -> 실패 메세지를 띄어준다, 성공 -> db상에 비밀번호 변경한다.
     */
    @ResponseBody
    @GetMapping("/password/change/result")
    public String passwordChangeResult(@CurrentMember Member member, String oldpass, String pass, String repass) {
        String message = "";
        if (member == null) {
            message = "로그인 되어있지 않습니다. 다시 로그인해주세요.";
        }
        if (!oldpass.isEmpty() && !pass.isEmpty() && !repass.isEmpty()) {
            if (passwordEncoder.matches(oldpass, member.getPassword())) {
                if (pass.matches("^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,}$")) {
                    if (pass.equals(repass)) {
                        member.setPassword(passwordEncoder.encode(pass));
                        memberRepository.save(member);
                        message = "비밀번호 변경 완료 되었습니다.";
                    } else {
                        message = "새로운 비밀번호가 서로 다릅니다. 다시 시도해주세요.";
                    }
                } else {
                    message = "패스워드는 영문자, 숫자, 특수기호를 조합하여 최소 8자 이상을 입력하셔야 합니다";
                }
            } else {
                message = "현재 비밀번호가 다릅니다.";
            }
        } else {
            message = "모두 입력하셔야 합니다.";
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message);
        return jsonObject.toString();
    }

    /**
     * 작성한 글, 댓글, 회원 정보를 모두 db상에서 삭제한다.
     * @author 민경
     * @param member 현재 로그인한 회원
     * @return 성공 -> db상에서 모두 삭제 후 idnex.html로 이동한다. 실패 -> member/mypage.html
     */
    @GetMapping("/memberDelete")
    @ResponseBody
    public String memberdelete(@CurrentMember Member member) {
        memberRepository.delete(member);
        SecurityContextHolder.clearContext();
        JsonObject jsonObject = new JsonObject();
        return jsonObject.toString();
    }

    /** 회원의 주소, 닉네임, 닉네임 공개 유무, 핸드폰 번호 정보를 변경한다.
     * @author 민경
     * @param postcode 변경할 우편번호
     * @param address 변경할 기본주소
     * @param detailAddress 변경할 상세주소
     * @param newnickname 변경할 닉네임
     * @param newtel 변경할 핸드폰 번호
     * @param nicknameOpen 변경할 닉네임 공개 유무
     * @param member 현재 로그인한 회원
     * @return 실패 -> alrt로 실패 이유를 띄어 준다 성공 -> db상에 변경후 변경한 값으로 화면 변경
     */
    @GetMapping("/mypage/change")
    @ResponseBody
    public String mypageChange(String postcode, String address, String detailAddress, String newnickname,
                               String newtel, String nicknameOpen, @CurrentMember Member member) {

        String message = "변경할 정보가 없습니다";

        if (!newnickname.isEmpty()) {
            if (member.getNickname() == null || !member.getNickname().equals(newnickname)) {
                boolean nickname = memberService.checkNickname(newnickname);
                if (newnickname != null && nickname == true) {
                    message = "중복된 닉네임 입니다";
                } else if (newnickname.length() > 10 || newnickname.length() < 2) {
                    message = "닉네임의 길이는 2자이상 10자 이하여야 합니다.";
                } else {
                    member.setNickname(newnickname);
                    memberRepository.save(member);
                    message = "변경 완료 되었습니다.";
                }
            }
        }
        if (!member.getTel().equals(newtel)) {
            member.setTel(newtel);
            memberRepository.save(member);
            message = "변경 완료 되었습니다.";
        }
        if (member.getUserAddress() == null || !member.getUserAddress().getPostcode().equals(postcode)
                || !member.getUserAddress().getBaseAddress().equals(address)
                || !member.getUserAddress().getDetailAddress().equals(detailAddress)) {
            if (!postcode.isEmpty() && !address.isEmpty() && !detailAddress.isEmpty()) {
                member.setUserAddress(UserAddress.builder()
                        .postcode(postcode)
                        .baseAddress(address)
                        .detailAddress(detailAddress)
                        .build());
                memberRepository.save(member);
                message = "변경 완료 되었습니다.";
            }
            if (postcode == null || address == null || detailAddress == null) {
                message = "주소를 모두 입력해주세요";
            }
        }

        if (!String.valueOf(member.isNicknameOpen()).equals(nicknameOpen)) {//닉네임 공개 유무 설정
            if (member.isNicknameOpen() == true) {
                member.setNicknameOpen(false);
                memberRepository.save(member);
                message = "변경 완료 되었습니다.";

            } else if (member.isNicknameOpen() == false) {
                if (member.getNickname() == null && newnickname.isEmpty()) {
                    message = "닉네임 설정후 변경할 수 있습니다.";
                } else {
                    member.setNicknameOpen(true);
                    memberRepository.save(member);
                    message = "변경 완료 되었습니다.";
                }
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message", message);
        return jsonObject.toString();
    }

    /**
     * @param tel   사용자로부터 입력받은 전화번호
     * @param model
     * @return 아이디찾기 페이지
     * @author miri
     * @author 미리
     */
    @RequestMapping("/idsearch")
    public String idSearchResult(String tel, Model model) {
        // 아이디 찾기

        log.info("tel : {}", tel);

        String message = null;

        try {

            if (tel != null) {
                Member member = memberRepository.findByTel(tel).get();
                String[] split = member.getUsername().split("@");
                String repeat = "*".repeat(split[0].length() - 2);
                message = "귀하의 가입하신 정보는 " + split[0].substring(0, 2) + repeat + "@" + split[1] + " 입니다";
            }

        } catch (NoSuchElementException e) {
            message = "등록된 정보가 없습니다";
        }

        model.addAttribute("message", message);

        log.info("메세지 : {}", message);

        return "member/idsearch";
    }
}