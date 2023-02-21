package site.devroad.softeer.src.roadmap.subject;

import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.exam.ExamRepo;
import site.devroad.softeer.src.exam.ExamSubmissionRepo;
import site.devroad.softeer.src.exam.model.Exam;
import site.devroad.softeer.src.exam.model.ExamSubmission;
import site.devroad.softeer.src.exam.model.SubmissionType;
import site.devroad.softeer.src.roadmap.RoadmapRepo;
import site.devroad.softeer.src.roadmap.course.Course;
import site.devroad.softeer.src.roadmap.course.CourseRepo;
import site.devroad.softeer.src.roadmap.dto.GetAllSubjects;
import site.devroad.softeer.src.roadmap.dto.GetSubjectDetailRes;
import site.devroad.softeer.src.roadmap.dto.domain.CourseDetail;
import site.devroad.softeer.src.roadmap.model.Roadmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {
    private final SubjectRepo subjectRepo;
    private final CourseRepo courseRepo;
    private final RoadmapRepo roadmapRepo;
    private final ExamSubmissionRepo examSubmissionRepo;
    private final ExamRepo examRepo;

    public SubjectService(SubjectRepo subjectRepo, CourseRepo courseRepo, RoadmapRepo roadmapRepo, ExamSubmissionRepo examSubmissionRepo, ExamRepo examRepo) {
        this.subjectRepo = subjectRepo;
        this.courseRepo = courseRepo;
        this.roadmapRepo = roadmapRepo;
        this.examSubmissionRepo = examSubmissionRepo;
        this.examRepo = examRepo;
    }

    public GetSubjectDetailRes getCourseDetails(Long subjectId, Long accountId) {
        Optional<Roadmap> roadmapById = roadmapRepo.findRoadmapByAccountId(accountId);
        if (roadmapById.isEmpty()) {
            throw new CustomException(ExceptionType.ROADMAP_NOT_FOUND);
        }
        List<Course> courses = courseRepo.findCoursesBySubjectId(subjectId);
        ArrayList<CourseDetail> courseDetails = new ArrayList<>();
        for (Course course : courses) {
            courseDetails.add(createCourseDetail(course));
        }
        //TODO: MCQ, FRQ 따로 빼기
        Optional<Exam> mcq = examRepo.findExamBySubjectId(subjectId, "MCQ");
        if (mcq.isEmpty()) {
            throw new CustomException(ExceptionType.EXAM_NOT_FOUND);
        }
        Optional<ExamSubmission> byExamIdAndAccountId = examSubmissionRepo.findByExamIdAndAccountId(mcq.get().getId(), accountId);

        //ExamSubmission이 존재하고 PASSED했다면 finish가 true
        if (byExamIdAndAccountId.isPresent()) {
            SubmissionType submissionType = byExamIdAndAccountId.get().getSubmissionType();
            if (submissionType.equals(SubmissionType.PASSED))
                return new GetSubjectDetailRes(courseDetails, true);
        }
        return new GetSubjectDetailRes(courseDetails, false);
    }

    public CourseDetail createCourseDetail(Course course) {
        return new CourseDetail(course);
    }

    public Optional<Subject> getSubject(Long subjectId) {
        return subjectRepo.findById(subjectId);
    }

    public GetAllSubjects getAllSubjects() {
        return new GetAllSubjects(subjectRepo.findAll());
    }
}
