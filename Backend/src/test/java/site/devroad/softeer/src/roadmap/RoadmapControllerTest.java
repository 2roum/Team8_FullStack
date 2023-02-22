package site.devroad.softeer.src.roadmap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import site.devroad.softeer.src.roadmap.chapter.Chapter;
import site.devroad.softeer.src.roadmap.course.Course;
import site.devroad.softeer.src.roadmap.course.CourseService;
import site.devroad.softeer.src.roadmap.dto.*;
import site.devroad.softeer.src.roadmap.dto.domain.ChapterDetail;
import site.devroad.softeer.src.roadmap.dto.domain.CourseDetail;
import site.devroad.softeer.src.roadmap.dto.domain.SubjectDetail;
import site.devroad.softeer.src.roadmap.subject.Subject;
import site.devroad.softeer.src.roadmap.subject.SubjectService;
import site.devroad.softeer.src.user.UserService;
import site.devroad.softeer.src.user.model.Account;
import site.devroad.softeer.utility.JwtUtility;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static site.devroad.softeer.src.exam.model.SubmissionType.PASSED;

@WebMvcTest(RoadmapController.class)
class RoadmapControllerTest {
    @MockBean
    JwtUtility jwtUtility;
    @MockBean
    RoadmapService roadmapService;
    @MockBean
    SubjectService subjectService;
    @MockBean
    CourseService courseService;
    @MockBean
    UserService userService;
    Account adminAccount = new Account( 10L, "ADMIN", -1L, "0109999", "Admin", null, null);
    Subject subject1 = new Subject(1L, "자료구조", "자료구조의 기초에 대해 배워봅시다");
    Subject subject2 = new Subject(2L, "프로그래밍 언어", "프로그래밍 언어에 대해 배워봅시다");


    SubjectDetail subjectDetail1 = new SubjectDetail("자료구조", 1L, PASSED, PASSED, 10L, 15L);
    SubjectDetail subjectDetail2 = new SubjectDetail("프로그래밍 언어", 2L, PASSED, PASSED, 10L, 15L);

    Course course1 = new Course(1L, 1L, "test1", "thumbnail1", "자구_test1", "descript_test1", 1L, "online");
    Course course2 = new Course(2L, 1L, "test2", "thumbnail2", "자구_test2", "descript_test2", 1L, "online");
    CourseDetail courseDetail1 = new CourseDetail(course1);
    CourseDetail courseDetail2 = new CourseDetail(course2);
    Chapter chapter1 = new Chapter(1L, 1L, "test1", "tt1", "thumb1", "desc1", 1);
    Chapter chapter2 = new Chapter(2L, 1L, "test2", "tt2", "thumb2", "desc2", 1);

    ChapterDetail chapterDetail1 = new ChapterDetail(chapter1, course1.getCourseName());
    ChapterDetail chapterDetail2 = new ChapterDetail(chapter2, course1.getCourseName());

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("로드맵의 주제들에 배워보기")
    void getRoadmapSubjects() throws Exception {
        //given
        given(roadmapService.getSubjects(any(Long.class))).willReturn(
                new GetRoadmapDetailRes(List.of(subjectDetail1, subjectDetail2))
        );

        //when
        mockMvc.perform(
                        get("/api/roadmap")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("subjects").exists())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행 여부를 체크
        verify(roadmapService).getSubjects(any(Long.class));
    }

    @Test
    @DisplayName("모든 서브젝트 받아오기")
    void getAllSubjects() throws Exception {
        //given
        given(subjectService.getAllSubjects()).willReturn(
                new GetAllSubjects(List.of(subject1, subject2))
        );

        //when
        mockMvc.perform(
                        get("/api/subject")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("subjects").exists())
                .andDo(print());

        //then
        verify(subjectService).getAllSubjects();

    }

    @Test
    @DisplayName("과목의 세부 정보를 불러오는 경우를 테스트")
    void getSubjectDetail() throws Exception {
        //given
        given(subjectService.getCourseDetails(any(Long.class), any(Long.class)))
                .willReturn(new GetSubjectDetailRes(List.of(courseDetail1, courseDetail2), false));

        //when
        mockMvc.perform(
                        get("/api/subject/10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("courses").exists())
                .andExpect(jsonPath("finish").exists())
                .andDo(print());

        //then
        verify(subjectService).getCourseDetails(10L, 0L);
    }

    @Test
    void getCourseDetail() throws Exception {
        //given
        given(courseService.getChapterDetails(any(Long.class), any(Long.class))).willReturn(List.of(chapterDetail1, chapterDetail2));
        given(jwtUtility.getAccountId("test-jwt")).willReturn(10L);

        //when
        mockMvc.perform(
                        get("/api/course/1")
                                .header("jwt", "test-jwt")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("chapters").exists())
                .andExpect(jsonPath("curChapterId").exists())
                .andDo(print());

        //then
        verify(courseService).getChapterDetails(1L, 10L);
    }

    @Test
    void finishChapter() throws Exception {
        //given
        given(courseService.putFinishChapter(any(Long.class), any(Long.class)))
                .willReturn(new PutChapterFinishRes(true, 101L));
        given(jwtUtility.getAccountId(any(String.class)))
                .willReturn(1L);
        //when
        mockMvc.perform(
                        put("/api/chapter/1")
                                .header("jwt", "1234")
                )
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("courseFinished").exists())
                .andExpect(jsonPath("nextChapterId").exists())
                .andDo(print());

        //then
        verify(courseService).putFinishChapter(1L, 1L);
    }

    @Test
    void createRoadmap() throws Exception{
        //when
        mockMvc.perform(
                        post("/api/roadmap")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"signupTest@gmail.com\"," +
                                        "\"subjectSequence\":[1,2,3,4,5]}")
                //content 적어주기
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("success").exists())
                .andDo(print());

        //then
        verify(roadmapService).createRoadmap(any(PostRoadmapReq.class));
    }

    @Test
    void getChapterDetail() throws Exception{
        given(courseService.getChapterDetail(any(Long.class), any(Long.class)))
                .willReturn(chapterDetail1);
        given(jwtUtility.getAccountId(any(String.class)))
                .willReturn(10L);

        //when
        mockMvc.perform(
                        get("/api/chapter/1")
                                .header("jwt", "jwt-10L")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andExpect(jsonPath("chapterDetail").exists())
                .andDo(print());

        //then
        verify(courseService).getChapterDetail(1L, 10L);
    }

    @Test
    void deleteRoadMap() throws Exception{
        //given
        given(userService.getAccountById(any(Long.class)))
                .willReturn(adminAccount);
        given(jwtUtility.getAccountId(any(String.class)))
                .willReturn(10L);

        //when
        mockMvc.perform(
                        delete("/api/roadmap/1")
                                .header("jwt", "jwt-10L")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").exists())
                .andDo(print());

        //then
        verify(userService).getAccountById(10L);
        verify(roadmapService).deleteRoadmapByAccountId(1L);
        verify(jwtUtility).getAccountId("jwt-10L");
    }
}