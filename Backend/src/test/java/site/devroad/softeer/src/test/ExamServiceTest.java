package site.devroad.softeer.src.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.ExamService;
import site.devroad.softeer.src.exam.ExamSubmissionRepo;
import site.devroad.softeer.src.exam.dto.PostAssignSubmitReq;
import site.devroad.softeer.src.roadmap.subject.Subject;
import site.devroad.softeer.src.roadmap.subject.SubjectRepo;
import site.devroad.softeer.src.user.UserRepo;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.src.user.model.LoginInfo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class ExamServiceTest {
    private static Logger logger = LoggerFactory.getLogger(ExamService.class);

    @Autowired
    ExamService examService;
    @Autowired
    UserRepo userRepo;

    @Autowired
    SubjectRepo subjectRepo;

    @Autowired
    ExamSubmissionRepo submissionRepo;


    @Test
    @DisplayName("사용자가 실제로 해당 테스트를 패스 했는지 성공여부를 확인.")
    void checkIfExamPassed() {
        //given
        LoginInfo loginInfo = userRepo.findLoginInfoByEmail("jm1234@naver.com").get();
        Optional<Account> accountById = userRepo.findAccountById(loginInfo.getAccountId());
        Account account = accountById.get();

        Subject subject = subjectRepo.findById(1L).get();
        //when
        try {
            Boolean result = examService.isUserPassedExam(subject.getId(), account.getId());

            //then
            assertEquals(result, true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Test
    @DisplayName("사용자가 Exam Submission을 잘 업로드 할 수 있는지 확인.")
    void examSubmissionTest() {
        try {
            //given
            Account account = userRepo.createAccountInfo("hi", "0100000092", "Student");
            userRepo.doSubscribe(account.getId());

            //when
            examService.submitAssignment(account.getId(), new PostAssignSubmitReq("wow.naver.com", "test_passed", 1L));

            //then
            assertEquals(subjectRepo.findById(1L).isPresent(), true);
        } catch (CustomException e) {
            logger.warn(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    @Test
    @DisplayName("Make Purchased")
    void makePurchase() {
        //given
        Account accountInfo = userRepo.createAccountInfo("hello", "01029846389", "Student");
        Long id = accountInfo.getId();


        try {
            examService.checkExamPurchased(id);
        } catch (CustomException e) {
            assertEquals(e.getExceptionType(), ExceptionType.EXAM_NOT_PURCHASED);
        }
        //when
        examService.makePurchasedByTossOrderId(String.valueOf(id) + "_123_asdfasdfasdf");

        //then
        //if error occurs below code it will throw exception
        examService.checkExamPurchased(id);


    }
}
