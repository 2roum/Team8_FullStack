package site.devroad.softeer.src.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.src.exam.ExamSubmissionRepo;
import site.devroad.softeer.src.roadmap.RoadmapRepo;
import site.devroad.softeer.src.roadmap.chapter.ChapterRepo;
import site.devroad.softeer.src.roadmap.completedchapter.CompletedChapterRepo;
import site.devroad.softeer.src.roadmap.subject.SubjectRepo;
import site.devroad.softeer.src.user.dto.PostSignInReq;
import site.devroad.softeer.src.user.dto.PostSignInRes;
import site.devroad.softeer.src.user.dto.PostSignUpReq;
import site.devroad.softeer.src.user.dto.PostSignUpRes;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.src.user.model.LoginInfo;
import site.devroad.softeer.utility.JwtUtility;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {UserService.class})
class UserServiceTest {
    private final String name = "test";
    private final String type = "Student";
    private final String phone = "01042427272";
    private final String email = "test@naver.com";
    private final String password = "1234";
    private final Long roadmapId = 12L;
    private final Long accountId = 1000L;
    private final Long loginInfoId = 50L;
    @MockBean
    UserRepo userRepo;
    @MockBean
    RoadmapRepo roadmapRepo;
    @MockBean
    SubjectRepo subjectRepo;
    @MockBean
    ExamSubmissionRepo submissionRepo;
    @MockBean
    CompletedChapterRepo completedChapterRepo;
    @MockBean
    ChapterRepo chapterRepo;
    @MockBean
    JwtUtility jwtUtility;
    @Autowired
    UserService userService;
    private Account account;
    private Account adminAccount;
    private LoginInfo loginInfo;

    @BeforeEach
    public void setUp() {
        account = new Account(accountId, name, roadmapId, phone, type, null, null);
        adminAccount = new Account(accountId + 1, "ADMIN", -1L, "0109999", "Admin", null, null);
        loginInfo = new LoginInfo(loginInfoId, email, password, accountId);
    }

    @Test
    @DisplayName("회원 가입 서비스 메서드 테스트")
    void join() {
        //given
        PostSignUpReq postSignUpReq = new PostSignUpReq(email, name, phone, password);

        //when
        Mockito.when(userRepo.findByPhone(phone)).thenReturn(Optional.empty());
        Mockito.when(userRepo.findLoginInfoByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(userRepo.createAccountInfo(name, phone, type))
                .thenReturn(account);
        Mockito.when(userRepo.createLoginInfo(email, password, accountId))
                .thenReturn(loginInfo);
        PostSignUpRes joinResDto = userService.join(postSignUpReq);

        //then
        assertThat(joinResDto.getUserId()).isEqualTo(accountId);
    }

    @Test
    @DisplayName("아이디에 해당하는 계정이 없어 로그인이 실패하는 경우")
    void signInFailCausedByEmail() {
        //given
        PostSignInReq postSignInReq = new PostSignInReq(email, password);

        //when
        Mockito.when(userRepo.findLoginInfoByEmail(postSignInReq.getEmail()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.signIn(postSignInReq))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("비밀번호가 틀려 로그인을 실패하는 경우")
    void signInFailCausedByPWD() {
        //given
        PostSignInReq postSignInReq = new PostSignInReq(email, password);

        //when
        String hashpw = BCrypt.hashpw("4566", BCrypt.gensalt());
        Mockito.when(userRepo.findLoginInfoByEmail(postSignInReq.getEmail()))
                .thenReturn(Optional.of(new LoginInfo(10L, email, hashpw, 1000L)));

        //then
        assertThatThrownBy(() -> userService.signIn(postSignInReq))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그인을 성공하는 경우")
    void signInSuccess() {
        //given
        PostSignInReq postSignInReq = new PostSignInReq(email, password);
        Account account = new Account(accountId, name, 12L, "1212", "Student", null, null);

        //when
        String hashpw = BCrypt.hashpw(password, BCrypt.gensalt());
        Mockito.when(userRepo.findLoginInfoByEmail(postSignInReq.getEmail()))
                .thenReturn(Optional.of(new LoginInfo(10L, email, hashpw, accountId)));
        Mockito.when(userRepo.findAccountById(accountId))
                .thenReturn(Optional.of(account));
        Mockito.when(jwtUtility.makeJwtToken(accountId, name))
                .thenReturn("tempJWT");

        //then
        PostSignInRes postSignInRes = userService.signIn(postSignInReq);
        assertThat(postSignInRes.getJwt()).isEqualTo("tempJWT");
    }

    @Test
    @DisplayName("중복된 핸드폰이 이미 존재하는 경우")
    void validateSignUpFailCausedByPhone() {
        //given
        String phone = "01023231313";
        String name = "hello";
        Long accountId = 100L;
        Account account = new Account(accountId, name, 12L, "1212", "Student", null, null);
        PostSignUpReq postSignUpReq = new PostSignUpReq("test@naver.com", name, phone, "1234");

        //when
        Mockito.when(userRepo.findByPhone(phone)).thenReturn(Optional.of(account));

        //then
        assertThatThrownBy(() -> userService.validateSignUp(postSignUpReq)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("중복된 이메일이 이미 존재하는 경우")
    void validateSignUpFailCausedByEmail() {
        //given
        String phone = "01023231313";
        String email = "test@naver.com";
        String name = "hello";
        String password = "1234";
        Long accountId = 100L;
        LoginInfo loginInfo = new LoginInfo(10L, email, password, accountId);
        PostSignUpReq postSignUpReq = new PostSignUpReq(email, name, phone, password);

        //when
        Mockito.when(userRepo.findByPhone(phone)).thenReturn(Optional.empty());
        Mockito.when(userRepo.findLoginInfoByEmail(email)).thenReturn(Optional.of(loginInfo));

        //then
        assertThatThrownBy(() -> userService.validateSignUp(postSignUpReq)).isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("관리자인 경우")
    void isAdmin() {

        //when
        Mockito.when(userRepo.findAccountById(accountId)).thenReturn(Optional.of(adminAccount));

        //then
        assertThat(userService.isAdmin(accountId)).isEqualTo(true);
    }

    @Test
    @DisplayName("로드맵이 없는 유저들 받아오기")
    void getNoRoadmapUsers() {
        //given
        LoginInfo loginInfo = new LoginInfo(1L, "hello@naver.com", "1234", 10L);
        List<LoginInfo> noRoadmapUsers = List.of(loginInfo);

        //when
        Mockito.when(userRepo.findNoRoadmapUser()).thenReturn(noRoadmapUsers);
        Mockito.when(userRepo.findAccountById(adminAccount.getId())).thenReturn(Optional.of(adminAccount));


        //then
        List<String> noRoadmapUser = userService.getNoRoadmapUsers(adminAccount.getId());
        assertThat(noRoadmapUser.size()).isEqualTo(1);
        assertThat(noRoadmapUser.get(0)).isEqualTo("hello@naver.com");
    }

    @Test
    void isUserSubscribe() {
        //given

        //when
        Mockito.when(userRepo.isUserSubscribed(accountId)).thenReturn(false);
        Mockito.when(userRepo.isUserSubscribed(adminAccount.getId())).thenReturn(true);

        //then
        assertThat(userService.isUserSubscribe(accountId)).isEqualTo(false);
        assertThat(userService.isUserSubscribe(adminAccount.getId())).isEqualTo(true);
    }

    @Test
    void getUserDetail() {

    }

    @Test
    void getAllUser() {
    }

    @Test
    void getAccountById() {
    }
}