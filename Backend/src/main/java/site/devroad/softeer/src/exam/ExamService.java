package site.devroad.softeer.src.exam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.dto.*;
import site.devroad.softeer.src.exam.dto.domain.Assignment;
import site.devroad.softeer.src.exam.dto.domain.ExamDetail;
import site.devroad.softeer.src.exam.dto.domain.MultiChoiceQuestion;
import site.devroad.softeer.src.exam.dto.domain.PeerDetail;
import site.devroad.softeer.src.exam.model.Exam;
import site.devroad.softeer.src.exam.model.ExamMcq;
import site.devroad.softeer.src.exam.model.ExamSubmission;
import site.devroad.softeer.src.exam.model.SubmissionType;
import site.devroad.softeer.src.roadmap.subject.Subject;
import site.devroad.softeer.src.roadmap.subject.SubjectRepo;
import site.devroad.softeer.src.user.UserRepo;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.utility.GithubUtility;
import site.devroad.softeer.utility.OpenAiUtility;

import java.util.*;

@Service
public class ExamService {

    private static Logger logger = LoggerFactory.getLogger(ExamService.class);
    private ExamRepo examRepo;
    private SubjectRepo subjectRepo;
    private ExamSubmissionRepo examSubmissionRepo;
    private UserRepo userRepo;
    private GithubUtility githubUtility;
    private OpenAiUtility openAiUtility;

    @Autowired
    public ExamService(ExamRepo examRepo, SubjectRepo subjectRepo, ExamSubmissionRepo examSubmissionRepo, UserRepo userRepo, GithubUtility githubUtility, OpenAiUtility openAiUtility) {
        this.examRepo = examRepo;
        this.subjectRepo = subjectRepo;
        this.examSubmissionRepo = examSubmissionRepo;
        this.userRepo = userRepo;
        this.githubUtility = githubUtility;
        this.openAiUtility = openAiUtility;
    }

    public Boolean isUserPassedExam(Long subjectId, Long accountId) {

        //subjectId -> subject
        Optional<Subject> optionalSubject = subjectRepo.findById(subjectId);
        if (optionalSubject.isEmpty())
            throw new CustomException(ExceptionType.SUBJECT_NOT_FOUND);

        Subject subject = optionalSubject.get();

        logger.warn("subject id : {}, subject description : {}", subject.getId(), subject.getDescription());

        //subject.id -> exam
        Optional<Exam> optionalExam = examRepo.findExamBySubjectId(subject.getId(), "MCQ");
        if (optionalExam.isEmpty())
            throw new CustomException(ExceptionType.EXAM_NOT_FOUND);

        Exam exam = optionalExam.get();
        logger.warn("exam id : {}, exam description : {}, accountId : {}", exam.getId(), exam.getDescription(), accountId);
        //exam.id + account.id -> examSubmission
        Optional<ExamSubmission> examSubmission = examSubmissionRepo.findByExamIdAndAccountId(exam.getId(), accountId);

        logger.warn("submit id : {}, {}", examSubmission.get().getId(), examSubmission.get().getSubmissionType());
        return examSubmission.filter(submission -> submission.getSubmissionType() == SubmissionType.PASSED).isPresent();


    }

    public ExamDetail getExamDetail(Long examId) {
        Optional<ExamDetail> examDetailById = examRepo.findExamDetailById(examId);
        if (examDetailById.isEmpty()) {
            throw new CustomException(ExceptionType.EXAM_NOT_FOUND);
        }
        ExamDetail examDetail = examDetailById.get();
        String type = examDetail.getType();
        if (type.equals("MCQ")) {
            List<ExamMcq> questionsByExamId = examRepo.findQuestionsByExamId(examId);
            return ExamDetail.createMCQDetail(examDetail, getAns(questionsByExamId), getMCQs(questionsByExamId));
        }
        return examDetail;
    }

    public List<Integer> getAns(List<ExamMcq> questions) {
        List<Integer> ans = new ArrayList<>();
        for (ExamMcq examMcq : questions) {
            ans.add(examMcq.getAns());
        }
        return ans;
    }

    public List<MultiChoiceQuestion> getMCQs(List<ExamMcq> questions) {
        List<MultiChoiceQuestion> ans = new ArrayList<>();
        for (ExamMcq examMcq : questions) {
            if (examMcq == null)
                continue;
            ans.add(new MultiChoiceQuestion(examMcq));
        }
        return ans;
    }

    public void checkExamPurchased(Long accountId) {
        if (!userRepo.isUserSubscribed(accountId)) {
            throw new CustomException(ExceptionType.EXAM_NOT_PURCHASED);
        }
    }

    public void submitAssignment(Long accountId, PostAssignSubmitReq req) {
        examRepo.addExamSubmission(accountId, req.getExamId(), req.getUrl(), req.getDescription());
    }

    public void makePurchasedByTossOrderId(String orderId) {
        //   orderId : accountId + "_" + examId + "_" + randomStr,
        String[] parsedOrderId = orderId.split("_");
        Long accountId = Long.valueOf(parsedOrderId[0]);
        if (!userRepo.isUserSubscribed(accountId)) {
            userRepo.doSubscribe(accountId);
        }
        userRepo.extendSubscribeEndDate(accountId, 31);
    }

    public PutExamDetailRes getExamDetailRes(PutExamDetailReq req, Long accountId) {
        Boolean result = req.getResult();
        Long examId = req.getExamId();
        SubmissionType submissionType = result ? SubmissionType.PASSED : SubmissionType.FAILED;
        if(examSubmissionRepo.findByExamIdAndAccountId(req.getExamId(), accountId).isPresent()){
            examSubmissionRepo.updateByExamIdAndAccountId(examId, accountId, submissionType);
        }
        else{
            examSubmissionRepo.addExamSubmission(req.getExamId(), accountId, submissionType);
        }
        return new PutExamDetailRes();
    }

    public GetAssignmentDetail getAssignmentDetail(Long examSubmissionId) {
        Optional<ExamSubmission> examSubmissionById = examSubmissionRepo.findExamSubmissionById(examSubmissionId);
        if (examSubmissionById.isEmpty())
            throw new CustomException(ExceptionType.EXAM_SUBMISSION_NOT_FOUND);
        ExamSubmission examSubmission = examSubmissionById.get();
        Optional<Account> accountById = userRepo.findAccountById(examSubmission.getAccountId());
        if (accountById.isEmpty())
            throw new CustomException(ExceptionType.ACCOUNT_NOT_FOUND);
        Account account = accountById.get();
        Assignment assignment = new Assignment(examSubmission, account.getName());
        return new GetAssignmentDetail(assignment);
    }

    @Async
    public void doAiReview(Long examSubmissionId) {
        Optional<ExamSubmission> optionalExamSubmission = examSubmissionRepo.findExamSubmissionById(examSubmissionId);
        if(optionalExamSubmission.isEmpty())
            throw new CustomException(ExceptionType.EXAM_SUBMISSION_NOT_FOUND);

        // https://github.com/rohsik2/Team8_FullStack
        ExamSubmission submission = optionalExamSubmission.get();

        if(submission.getUrl().contains("/issues/"))
            throw new CustomException(ExceptionType.AI_REVIEW_ALREADY_DONE);
        String[] splitedURl = submission.getUrl().split("/");
        String username = splitedURl[3];
        String repos = splitedURl[4];

        logger.info("username {}, repose {}", username, repos);

        //get main file type from url
        String extension = githubUtility.getMainExtensionFromRepo(username, repos);

        //Making code summary from open ai
        Map<String, String> repo = githubUtility.getAllCodeFromRepo(username, repos, extension);
        Map<String, String> summaries = new HashMap<>();
        for(String key : repo.keySet()) {
            String summary = openAiUtility.getCodeSummary(repo.get(key));
            summaries.put(key, summary);
        }

        //Insert summary info body
        String title = "Code Review from DevRoad";
        StringBuilder body = new StringBuilder();
        body.append("## Code Summary");
        for(String key : summaries.keySet()){
            body.append("\n\n### filename : [" + key + "]("+submission.getUrl()+"/blob/main"+key+")\n");
            body.append(summaries.get(key) + "\n");
        }

        //making code Review From open ai
        Map<String, String> reviews = new HashMap<>();
        for(String key : repo.keySet()){
            String review = openAiUtility.getCodeReview(repo.get(key));
            reviews.put(key, review);
        }

        //insert Review into body
        body.append("## Code Review");
        for(String key : summaries.keySet()){
            body.append("\n\n### filename : [" + key + "]("+submission.getUrl()+"/blob/main"+key+")\n");
            body.append(reviews.get(key) + "\n");
        }

        String issueUrl = githubUtility.createIssue(username, repos, title, body.toString());

        examSubmissionRepo.updateSubmissionUrl(submission.getId(), issueUrl);

    }

    public GetPeerDetail getPeerDetail(Long examId){

        List<PeerDetail> peerList = userRepo.findPeerDetailByExamId(examId);
        Collections.shuffle(peerList);
        return new GetPeerDetail(true , peerList.subList(0,2));
    }

}