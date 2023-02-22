package site.devroad.softeer.src.roadmap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.ExamRepo;
import site.devroad.softeer.src.exam.ExamSubmissionRepo;
import site.devroad.softeer.src.exam.model.Exam;
import site.devroad.softeer.src.exam.model.ExamSubmission;
import site.devroad.softeer.src.exam.model.SubmissionType;
import site.devroad.softeer.src.roadmap.dto.GetRoadmapDetailRes;
import site.devroad.softeer.src.roadmap.dto.PostRoadmapReq;
import site.devroad.softeer.src.roadmap.dto.domain.SubjectDetail;
import site.devroad.softeer.src.roadmap.model.Roadmap;
import site.devroad.softeer.src.roadmap.subject.Subject;
import site.devroad.softeer.src.roadmap.subject.SubjectRepo;
import site.devroad.softeer.src.user.UserRepo;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.src.user.model.LoginInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoadmapService {

    private final static String MCQ = "MCQ";
    private final static String FRQ = "FRQ";
    private static Logger logger = LoggerFactory.getLogger(RoadmapService.class);
    private final RoadmapRepo roadmapRepo;
    private final SubjectRepo subjectRepo;
    private final ExamRepo examRepo;
    private final ExamSubmissionRepo examSubmissionRepo;
    private final UserRepo userRepo;

    public RoadmapService(RoadmapRepo roadmapRepo, SubjectRepo subjectRepo, ExamRepo examRepo, ExamSubmissionRepo examSubmissionRepo, UserRepo userRepo) {
        this.roadmapRepo = roadmapRepo;
        this.subjectRepo = subjectRepo;
        this.examRepo = examRepo;
        this.examSubmissionRepo = examSubmissionRepo;
        this.userRepo = userRepo;
    }

    public GetRoadmapDetailRes getSubjects(Long accountId) {
        Optional<Account> accountById = userRepo.findAccountById(accountId);
        if (accountById.isEmpty())
            throw new CustomException(ExceptionType.ACCOUNT_NOT_FOUND);
        Account account = accountById.get();
        Long roadMapId = account.getRoadMapId();
        Optional<Roadmap> roadmapById = roadmapRepo.findRoadmapById(roadMapId);
        Boolean userSubscribed = userRepo.isUserSubscribed(accountId);
        if (roadmapById.isEmpty()) {
            throw new CustomException(ExceptionType.ROADMAP_NOT_FOUND);
        }
        Roadmap roadmap = roadmapById.get();
        Long roadmapId = roadmap.getId();
        List<Subject> subjectsByRoadmapId = subjectRepo.findSubjectsByRoadmapId(roadmapId);
        List<SubjectDetail> subjectDetails = new ArrayList<>();
        for (Subject subject : subjectsByRoadmapId) {
            //accountId와 subjectId를 가지고 조회하기
            String subjectName = subject.getName();
            Long subjectId = subject.getId();
            Optional<Exam> mcq = examRepo.findExamBySubjectIdAndType(subjectId, MCQ);
            Optional<Exam> frq = examRepo.findExamBySubjectIdAndType(subjectId, FRQ);
            if (mcq.isEmpty() || frq.isEmpty())
                throw new CustomException(ExceptionType.EXAM_NOT_FOUND);
            
            Exam mcqExam = mcq.get();
            Exam frqExam = frq.get();
            Optional<ExamSubmission> mcqSubmission = examSubmissionRepo.findByExamIdAndAccountId(mcqExam.getId(), accountId);
            Optional<ExamSubmission> frqSubmission = examSubmissionRepo.findByExamIdAndAccountId(frqExam.getId(), accountId);
            //디폴트 상태인 NONE
            SubmissionType mcqSubmissionType = SubmissionType.NONE;
            SubmissionType frqSubmissionType = SubmissionType.NONE;

            //subscribe를 한 상태에서는 제출이 존재한다면 submission type을 받아옴
            if (userSubscribed) {
                mcqSubmissionType = mcqSubmission.isPresent() ? mcqSubmission.get().getSubmissionType() : SubmissionType.PURCHASED;
                frqSubmissionType = frqSubmission.isPresent() ? frqSubmission.get().getSubmissionType() : SubmissionType.PURCHASED;
            }
            subjectDetails.add(new SubjectDetail(subjectName, subjectId, mcqSubmissionType, frqSubmissionType, mcqExam.getId(), frqExam.getId()));
        }
        return new GetRoadmapDetailRes(subjectDetails);
    }

    public Long getCurChapterId(Long accountId) {
        Optional<Roadmap> roadmapByAccountId = roadmapRepo.findRoadmapByAccountId(accountId);
        if (roadmapByAccountId.isEmpty())
            throw new CustomException(ExceptionType.ROADMAP_NOT_FOUND);
        return roadmapByAccountId.get().getChapterId();
    }

    public void setCurChapterId(Long accountId, Long curChapterId) {
        Optional<Roadmap> roadmapByAccountId = roadmapRepo.findRoadmapByAccountId(accountId);
        if (roadmapByAccountId.isEmpty())
            throw new CustomException(ExceptionType.ROADMAP_NOT_FOUND);
        Long roadmapId = roadmapByAccountId.get().getId();
        roadmapRepo.updateCurChapterId(roadmapId, curChapterId);
    }

    public void createRoadmap(PostRoadmapReq roadmapReq) {
        Optional<LoginInfo> loginInfo = userRepo.findLoginInfoByEmail(roadmapReq.getEmail());
        if (loginInfo.isEmpty()) {
            throw new CustomException(ExceptionType.ACCOUNT_NOT_FOUND);
        }
        Optional<Account> accountById = userRepo.findAccountById(loginInfo.get().getAccountId());
        if (accountById.isEmpty())
            throw new CustomException(ExceptionType.ACCOUNT_NOT_FOUND);
        Account account = accountById.get();
        Long roadmapId = roadmapRepo.createRoadmap(account.getName() + "'s roadmap");

        userRepo.setRoadmap(account.getId(), roadmapId);

        for (int i = 0; i < roadmapReq.getSubjectSequence().size(); i++) {
            roadmapRepo.addSubjectToRoadMap(roadmapId, roadmapReq.getSubjectSequence().get(i), i + 1);
        }
    }

    public void deleteRoadmapByAccountId(Long targetAccountId) {
        Optional<Roadmap> roadmapOptional = roadmapRepo.findRoadmapByAccountId(targetAccountId);
        if(roadmapOptional.isEmpty())
            return;
        roadmapRepo.deleteRoadmap(roadmapOptional.get().getId());

    }
}
