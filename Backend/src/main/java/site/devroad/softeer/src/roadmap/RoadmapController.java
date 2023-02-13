package site.devroad.softeer.src.roadmap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.src.roadmap.subject.Subject;
import site.devroad.softeer.src.roadmap.course.CourseService;
import site.devroad.softeer.src.roadmap.subject.SubjectService;
import site.devroad.softeer.src.roadmap.dto.*;
import site.devroad.softeer.src.roadmap.dto.domain.ChapterDetail;
import site.devroad.softeer.src.roadmap.dto.domain.CourseDetail;

import java.util.List;
import java.util.Map;

@RestController
public class RoadmapController {

    private final RoadmapService roadmapService;
    private final SubjectService subjectService;
    private final CourseService courseService;

    public RoadmapController(RoadmapService roadmapService, SubjectService subjectService, CourseService courseService) {
        this.roadmapService = roadmapService;
        this.subjectService = subjectService;
        this.courseService = courseService;
    }

    @GetMapping("/api/roadmap")
    public ResponseEntity<?> getRoadmapSubjects(@RequestAttribute(value = "accountId") Long accountId) {
        try {
            Map<String, List<List<Object>>> subjects = roadmapService.getSubjects(accountId);
            return new ResponseEntity<>(new GetRoadmapDetailRes(subjects), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/subject")
    public ResponseEntity<?> getAllSubjects() {
        List<Subject> allSubjects = subjectService.getAllSubjects();
        return new ResponseEntity<>(new GetAllSubjects(allSubjects), HttpStatus.OK);
    }

    @GetMapping("/api/subject/{subjectId}")
    public ResponseEntity<?> getSubjectDetail(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("subjectId") String subjectId) {
        try {
            List<CourseDetail> courses = subjectService.getCourseDetails(Long.valueOf(subjectId), accountId);
            return new ResponseEntity<>(new GetSubjectDetailRes(courses), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/course/{courseId}")
    public ResponseEntity<?> getCourseDetail(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("courseId") String courseId) {
        try {
            List<ChapterDetail> chapterDetails = courseService.getChapterDetails(Long.valueOf(courseId));
            Long curChapterId = roadmapService.getCurChapterId(accountId);
            return new ResponseEntity<>(new GetCourseDetailRes(chapterDetails, curChapterId), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @PutMapping("/api/chapter/{chapterId}")
    public ResponseEntity<?> finishChapter(@RequestAttribute(value = "accountId") Long accountId, @PathVariable("chapterId") String chapterId) {
        try {
            Long chapterIdL = Long.valueOf(chapterId);
            Long nextChapterId = courseService.getNextChapterId(accountId, chapterIdL);
            roadmapService.setCurChapterId(accountId, nextChapterId);
            Boolean courseFinished = nextChapterId.equals(-1L);
            return new ResponseEntity<>(new PutChapterFinishRes(courseFinished, nextChapterId), HttpStatus.ACCEPTED);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @PostMapping("/api/roadmap")
    public ResponseEntity<?> createRoadmap(@RequestBody PostRoadmapReq roadmapReq){
        try {
            roadmapService.createRoadmap(roadmapReq);
            return new ResponseEntity<>(new PostRoadmapRes(true), HttpStatus.CREATED);
        }catch(CustomException e){
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/chapter/{chapterId}")
    public ResponseEntity<?> getChapterDetail(@PathVariable("chapterId") String chapterId) {
        try {
            Long chapterIdL = Long.valueOf(chapterId);
            ChapterDetail chapterDetail = courseService.getChapterDetail(chapterIdL);
            return new ResponseEntity<>(new GetChapterDetailRes(chapterDetail), HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }
}
