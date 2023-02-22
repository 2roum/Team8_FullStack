package site.devroad.softeer.src.exam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import site.devroad.softeer.src.exam.dto.GetExamDetailRes;
import site.devroad.softeer.src.exam.dto.PostAssignSubmitReq;
import site.devroad.softeer.src.exam.dto.PutExamDetailReq;
import site.devroad.softeer.src.exam.dto.domain.ExamDetail;
import site.devroad.softeer.src.roadmap.RoadmapService;
import site.devroad.softeer.src.user.UserService;
import site.devroad.softeer.utility.JwtUtility;
import site.devroad.softeer.utility.TossUtility;

import java.util.Map;

@RestController
public class ExamController {
    private static Logger logger = LoggerFactory.getLogger(ExamController.class);
    private ExamService examService;
    private UserService userService;
    private JwtUtility jwtUtility;
    private RoadmapService roadmapService;
    private TossUtility tossUtility;

    @Autowired
    public ExamController(ExamService examService, UserService userService, JwtUtility jwtUtility, RoadmapService roadmapService, TossUtility tossUtility) {
        this.examService = examService;
        this.userService = userService;
        this.jwtUtility = jwtUtility;
        this.roadmapService = roadmapService;
        this.tossUtility = tossUtility;
    }

    @GetMapping("/api/exam/{examId}")
    public ResponseEntity<?> getExamDetail(@PathVariable("examId") Long examId, @RequestAttribute Long accountId) {

        examService.checkExamPurchased(accountId);
        ExamDetail examDetail = examService.getExamDetail(examId);
        return new ResponseEntity<>(new GetExamDetailRes(true, examDetail), HttpStatus.OK);
    }

    @PostMapping("/api/exam/assignment")
    public ResponseEntity<?> assignmentSubmit(@RequestAttribute Long accountId, @RequestBody PostAssignSubmitReq req) {
        examService.checkExamPurchased(accountId);
        examService.submitAssignment(accountId, req);
        return new ResponseEntity<>(Map.of("success", true), HttpStatus.CREATED);
    }

    @GetMapping("/api/exam/assignment/{examSubmissionId}")
    public ResponseEntity<?> getAssignmentDetail(@PathVariable("examSubmissionId") Long examSubmissionId) {
        return new ResponseEntity<>(examService.getAssignmentDetail(examSubmissionId), HttpStatus.OK);
    }

    @GetMapping("/api/purchase/exam/success")
    public ResponseEntity<?> purchaseSuccess(@RequestParam String orderId, @RequestParam String paymentKey, @RequestParam Integer amount) {
        logger.info("staring Toss server validation \norder_id : {}\n paymentKey : {} \n amount : {}", orderId, paymentKey, amount);
        tossUtility.validateTossParams(orderId, paymentKey, amount);
        examService.makePurchasedByTossOrderId(orderId);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("Location", "https://devroad.site/roadmap");
        return new ResponseEntity<>("", map, HttpStatus.TEMPORARY_REDIRECT);
    }

    @GetMapping("/api/purchase/exam/fail")
    public ResponseEntity<?> purchaseFail() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("Location", "/roadmap");
        return new ResponseEntity<>("", map, HttpStatus.TEMPORARY_REDIRECT);
    }

    @PutMapping("/api/exam/result")
    public ResponseEntity<?> putExamResult(@RequestAttribute Long accountId, @RequestBody PutExamDetailReq req) {
        return new ResponseEntity<>(examService.getExamDetailRes(req, accountId), HttpStatus.ACCEPTED);
    }


    @PostMapping("/api/exam/ai/{examSubmissionId}")
    public ResponseEntity<?> doSubmissionAI(@PathVariable("examSubmissionId") Long examSubmissionId){
        Thread runnable = new Thread(
                ()->{
                    examService.doAiReview(examSubmissionId);
                }
        );
        runnable.run();
        return new ResponseEntity<>(Map.of("success", true), HttpStatus.OK);
    }

    @GetMapping("/api/exam/ai/{examSubmissionId}")
    public ResponseEntity<?> isAIReviewFinish(@PathVariable("examSubmissionId") Long examSubmissionId){
        String url = examService.getAssignmentDetail(examSubmissionId).getAssignment().getUrl();
        if(url.contains("/issues/"))
            return new ResponseEntity<>(Map.of("success", true, "issueUrl", url), HttpStatus.OK);
        if(!url.contains("github"))
            return new ResponseEntity<>(Map.of("success", true, "issueUrl", url), HttpStatus.OK);
        return new ResponseEntity<>(Map.of("success", false), HttpStatus.OK);
    }


    @GetMapping("/api/exam/peer/{examId}")
    public ResponseEntity<?> getPeerDetail(@RequestAttribute Long accountId, @PathVariable("examId") Long examId){
        return new ResponseEntity<>(examService.getPeerDetail(accountId, examId),HttpStatus.OK);
    }
}
