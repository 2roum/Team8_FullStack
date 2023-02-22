package site.devroad.softeer.src.roadmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.roadmap.course.CourseService;
import site.devroad.softeer.src.roadmap.dto.*;
import site.devroad.softeer.src.roadmap.dto.domain.ChapterDetail;
import site.devroad.softeer.src.roadmap.subject.SubjectService;
import site.devroad.softeer.src.user.UserService;

import java.util.List;
import java.util.Map;

@RestController
public class RoadmapController {
    private final RoadmapService roadmapService;
    private final SubjectService subjectService;
    private final CourseService courseService;
    private final UserService userService;

    @Autowired
    public RoadmapController(RoadmapService roadmapService, SubjectService subjectService, CourseService courseService, UserService userService) {
        this.roadmapService = roadmapService;
        this.subjectService = subjectService;
        this.courseService = courseService;
        this.userService = userService;
    }

    @GetMapping("/api/roadmap")
    public ResponseEntity<?> getRoadmapSubjects(@RequestAttribute(value = "accountId") Long accountId) {
        return new ResponseEntity<>(roadmapService.getSubjects(accountId), HttpStatus.OK);
    }

    @GetMapping("/api/subject")
    public ResponseEntity<?> getAllSubjects() {
        return new ResponseEntity<>(subjectService.getAllSubjects(), HttpStatus.OK);
    }

    @GetMapping("/api/subject/{subjectId}")
    public ResponseEntity<?> getSubjectDetail(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("subjectId") Long subjectId) {
        GetSubjectDetailRes courseDetails = subjectService.getCourseDetails(subjectId, accountId);
        return new ResponseEntity<>(courseDetails, HttpStatus.OK);
    }

    @GetMapping("/api/course/{courseId}")
    public ResponseEntity<?> getCourseDetail(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("courseId") Long courseId) {
        List<ChapterDetail> chapterDetails = courseService.getChapterDetails(courseId, accountId);
        Long curChapterId = roadmapService.getCurChapterId(accountId);
        return new ResponseEntity<>(new GetCourseDetailRes(chapterDetails, curChapterId), HttpStatus.OK);
    }

    @PutMapping("/api/chapter/{chapterId}")
    public ResponseEntity<?> finishChapter(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("chapterId") Long chapterId) {
        PutChapterFinishRes putChapterFinishRes = courseService.putFinishChapter(accountId, chapterId);
        roadmapService.setCurChapterId(accountId, putChapterFinishRes.getNextChapterId());
        return new ResponseEntity<>(putChapterFinishRes, HttpStatus.ACCEPTED);
    }

    @PostMapping("/api/roadmap")
    public ResponseEntity<?> createRoadmap(@RequestBody PostRoadmapReq roadmapReq) {
        roadmapService.createRoadmap(roadmapReq);
        return new ResponseEntity<>(new PostRoadmapRes(true), HttpStatus.CREATED);
    }

    @GetMapping("/api/chapter/{chapterId}")
    public ResponseEntity<?> getChapterDetail(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("chapterId") Long chapterId) {
        ChapterDetail chapterDetail = courseService.getChapterDetail(chapterId, accountId);
        roadmapService.setCurChapterId(accountId, chapterId);
        return new ResponseEntity<>(new GetChapterDetailRes(chapterDetail), HttpStatus.OK);
    }

    @DeleteMapping("/api/roadmap/{accountId}")
    public ResponseEntity<?> deleteRoadMap(@RequestAttribute(value = "accountId") Long adminId, @PathVariable("accountId") Long targetAccountId) {
        if (!userService.getAccountById(adminId).getType().equals("Admin"))
            return new CustomException(ExceptionType.NO_ADMIN_USER).getResponseEntity();

        roadmapService.deleteRoadmapByAccountId(targetAccountId);
        return new ResponseEntity<>(Map.of("success", true), HttpStatus.OK);
    }
}
