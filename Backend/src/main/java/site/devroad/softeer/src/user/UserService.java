package site.devroad.softeer.src.user;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.ExamSubmissionRepo;
import site.devroad.softeer.src.exam.model.ExamSubmission;
import site.devroad.softeer.src.exam.model.SubmissionType;
import site.devroad.softeer.src.roadmap.RoadmapRepo;
import site.devroad.softeer.src.roadmap.chapter.ChapterRepo;
import site.devroad.softeer.src.roadmap.completedchapter.CompletedChapterRepo;
import site.devroad.softeer.src.roadmap.course.CourseRepo;
import site.devroad.softeer.src.roadmap.model.Roadmap;
import site.devroad.softeer.src.roadmap.subject.SubjectRepo;
import site.devroad.softeer.src.user.dto.*;
import site.devroad.softeer.src.user.dto.domain.UserDetail;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.src.user.model.LoginInfo;
import site.devroad.softeer.utility.JwtUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepo userRepo;
    private final RoadmapRepo roadmapRepo;
    private final SubjectRepo subjectRepo;
    private final ExamSubmissionRepo examSubmissionRepo;
    private final CompletedChapterRepo completedChapterRepo;
    private final CourseRepo courseRepo;
    private final ChapterRepo chapterRepo;
    private final JwtUtility jwtUtility;

    public UserService(UserRepo userRepo, RoadmapRepo roadmapRepo, SubjectRepo subjectRepo, ExamSubmissionRepo examSubmissionRepo,
                       CompletedChapterRepo completedChapterRepo, CourseRepo courseRepo, ChapterRepo chapterRepo, JwtUtility jwtUtility) {
        this.userRepo = userRepo;
        this.roadmapRepo = roadmapRepo;
        this.subjectRepo = subjectRepo;
        this.examSubmissionRepo = examSubmissionRepo;
        this.completedChapterRepo = completedChapterRepo;
        this.courseRepo = courseRepo;
        this.chapterRepo = chapterRepo;
        this.jwtUtility = jwtUtility;
    }

    public PostSignUpRes join(PostSignUpReq req) {
        validateSignUp(req);
        Account student = userRepo.createAccountInfo(req.getName(), req.getPhone(), "Student");
        String hashPassword = BCrypt.hashpw(req.getPassword(), BCrypt.gensalt());
        return new PostSignUpRes(userRepo.createLoginInfo(req.getEmail(), hashPassword, student.getId()).getId());
    }

    public PostSignInRes signIn(PostSignInReq req) {
        String email = req.getEmail();
        Optional<LoginInfo> loginInfo = userRepo.findLoginInfoByEmail(email);
        if (loginInfo.isEmpty()) {
            throw new CustomException(ExceptionType.ACCOUNT_NOT_FOUND);
        }
        String password = req.getPassword();
        boolean authentication = BCrypt.checkpw(password, loginInfo.get().getPassword());
        if (authentication) {
            Long accountId = loginInfo.get().getAccountId();
            Account accountById = getAccountById(accountId);
            String jwt = jwtUtility.makeJwtToken(accountId, accountById.getName());
            boolean admin = isAdmin(accountId);
            return new PostSignInRes(jwt, admin);
        }
        throw new CustomException(ExceptionType.AUTHENTICATION_FAILED);
    }

    public void validateSignUp(PostSignUpReq req) {
        String phone = req.getPhone();
        String email = req.getEmail();
        if (userRepo.findByPhone(phone).isPresent())
            throw new CustomException(ExceptionType.POST_ACCOUNT_PHONE_DUPLICATED);
        if (userRepo.findLoginInfoByEmail(email).isPresent())
            throw new CustomException(ExceptionType.POST_ACCOUNT_EMAIL_DUPLICATED);
    }

    public boolean isAdmin(Long accountId) {
        Account accountById = userRepo.findAccountById(accountId);
        return accountById.getType().equals("Admin");
    }

    public List<String> getNoRoadmapUsers() {
        List<LoginInfo> noRoadmapUser = userRepo.findNoRoadmapUser();
        List<String> users = new ArrayList<>();
        for (LoginInfo loginInfo : noRoadmapUser) {
            users.add(loginInfo.getEmail());
        }
        return users;
    }

    public Boolean isUserSubscribe(Long accountId) {
        return userRepo.isUserSubscribed(accountId);
    }

    public GetUserDetailRes getUserDetail(Long accountId) throws CustomException {
        Account accountById = userRepo.findAccountById(accountId);
        String userName = accountById.getName();
        Optional<Roadmap> roadmapByAccountId = roadmapRepo.findRoadmapByAccountId(accountId);
        //roadmapId가 없다면 subscribe 여부는 false
        if (roadmapByAccountId.isEmpty())
            return GetUserDetailRes.createNoRoadmapUserDetail(accountId, userName, false);

        Roadmap roadmap = roadmapByAccountId.get();
        Long roadmapId = roadmap.getId();
        Long chapterId = roadmap.getChapterId();
        int totalSubjects = subjectRepo.findSubjectsByRoadmapId(roadmapId).size();
        Boolean userSubscribe = isUserSubscribe(accountId);
        if (chapterId == 0) {
            return GetUserDetailRes.createNotStartUserDetail(accountId, roadmapId, (long) totalSubjects, userName, userSubscribe);
        }

        GetUserDetailRes getUserDetailRes = GetUserDetailRes.createUserDetail();
        getUserDetailRes.setSubscribe(userSubscribe);
        getUserDetailRes.setUserId(accountId);
        getUserDetailRes.setUserName(userName);
        getUserDetailRes.setRoadmapId(accountById.getRoadMapId());
        getUserDetailRes.setCurChapterPK(chapterId);
        getUserDetailRes.setTotalSubjectIdx((long) totalSubjects);
        List<ExamSubmission> mcqSubmissions = examSubmissionRepo.findMCQByRoadmapIdAndAccountId(roadmapId, accountId);
        long cnt = mcqSubmissions.stream()
                .filter(examSubmission -> examSubmission.getSubmissionType() == SubmissionType.PASSED)
                .count();
        getUserDetailRes.setCurSubjectIdx(cnt);

        //진행 중인 챕터가 없을 때와 한 챕터가 종료 되었을 때 분리
        if (chapterId == -1) {
            getUserDetailRes.setChapterPercent(1F);
            return getUserDetailRes;
        }
        Long courseId = chapterRepo.findChapterById(chapterId).get().getCourseId();
        int totalChapterCnt = chapterRepo.findChaptersByCourseId(courseId).size();
        //완료된 completed chatpers
        int completedChapterCnt = completedChapterRepo.readCompletedChapters(accountId, courseId).size();
        getUserDetailRes.setChapterPercent(completedChapterCnt / (float) totalChapterCnt);
        return getUserDetailRes;
    }

    public List<UserDetail> getAllUser(Long accountId) {
        List<UserDetail> allUser = userRepo.findAllUser();
        return allUser;
    }

    public Account getAccountById(Long accountId) {
        return userRepo.findAccountById(accountId);
    }
}
