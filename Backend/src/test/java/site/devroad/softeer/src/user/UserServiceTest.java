package site.devroad.softeer.src.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = {UserService.class})
class UserServiceTest {
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

    @Test
    @DisplayName("회원 가입 서비스 메서드 테스트")
    void join() {
        //given
        String name = "test";
        String type = "Student";
        String phone = "01042427272";
        String email = "test@naver.com";
        String password = "1234";
        Long roadmapId = 12L;
        Long accountId = 1000L;
        Long loginInfoId = 50L;

        PostSignUpReq postSignUpReq = new PostSignUpReq(email, name, phone, password);

        //when
        Mockito.when(userRepo.findByPhone(phone)).thenReturn(Optional.empty());
        Mockito.when(userRepo.findLoginInfoByEmail(email)).thenReturn(Optional.empty());
        Mockito.when(userRepo.createAccountInfo(name, phone, type))
                .thenReturn(new Account(accountId, name, roadmapId, phone, type, null, null));
        Mockito.when(userRepo.createLoginInfo(email, password, accountId))
                .thenReturn(new LoginInfo(loginInfoId, email, password, accountId));
        PostSignUpRes joinResDto = userService.join(postSignUpReq);

        //then
        assertThat(joinResDto.getUserId()).isEqualTo(accountId);
    }

    @Test
    @DisplayName("아이디에 해당하는 계정이 없어 로그인이 실패하는 경우")
    void signInFail() {
        //given
        PostSignInReq postSignInReq = new PostSignInReq("test@naver.com", "1234");

        //when
        Mockito.when(userRepo.findLoginInfoByEmail(postSignInReq.getEmail()))
                .thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> userService.signIn(postSignInReq))
                .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("로그인이 성공하는 경우")
    void signInSuccess() {

    }

    @Test
    void validateSignUp() {
    }

    @Test
    void isAdmin() {
    }

    @Test
    void getNoRoadmapUsers() {
    }

    @Test
    void isUserSubscribe() {
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