package site.devroad.softeer.src.roadmap.course;

import org.springframework.stereotype.Service;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.roadmap.RoadmapRepo;
import site.devroad.softeer.src.roadmap.chapter.Chapter;
import site.devroad.softeer.src.roadmap.chapter.ChapterRepo;
import site.devroad.softeer.src.roadmap.completedchapter.CompletedChapter;
import site.devroad.softeer.src.roadmap.completedchapter.CompletedChapterRepo;
import site.devroad.softeer.src.roadmap.dto.PutChapterFinishRes;
import site.devroad.softeer.src.roadmap.dto.domain.ChapterDetail;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    public static final Long FINISHED = -1L;
    private final RoadmapRepo roadmapRepo;
    private final ChapterRepo chapterRepo;
    private final CourseRepo courseRepo;
    private final CompletedChapterRepo completedChapterRepo;

    public CourseService(RoadmapRepo roadmapRepo, ChapterRepo chapterRepo, CourseRepo courseRepo, CompletedChapterRepo completedChapterRepo) {
        this.roadmapRepo = roadmapRepo;
        this.chapterRepo = chapterRepo;
        this.courseRepo = courseRepo;
        this.completedChapterRepo = completedChapterRepo;
    }

    public List<ChapterDetail> getChapterDetails(Long courseId, Long accountId) {
        return chapterRepo.findChapterDetailByCourseId(courseId, accountId);
    }

    public Optional<Chapter> getNextChapter(Long chapterId) {
        Optional<Chapter> chapterById = chapterRepo.findChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
        }
        Chapter chapter = chapterById.get();
        Long courseId = chapter.getCourseId();
        Integer sequence = chapter.getSequence();
        return chapterRepo.findNextChapter(courseId, sequence + 1);
    }

    public Chapter getChapter(Long chapterId) {
        Optional<Chapter> chapterById = chapterRepo.findChapterById(chapterId);
        if (chapterById.isEmpty()) {
            throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
        }
        return chapterById.get();
    }

    public ChapterDetail getChapterDetail(Long chapterId, Long accountId) {
        Optional<ChapterDetail> chapterDetailById = chapterRepo.findChapterDetailById(chapterId, accountId);
        if (chapterDetailById.isEmpty()) {
            Optional<Chapter> chapterById = chapterRepo.findChapterById(chapterId);
            if (chapterById.isEmpty())
                throw new CustomException(ExceptionType.CHAPTER_NOT_FOUND);
            Course course = courseRepo.findCourseById(chapterById.get().getCourseId()).get();
            return new ChapterDetail(chapterById.get(), course.getCourseName());
        }
        ChapterDetail chapterDetail = chapterDetailById.get();
        chapterDetail.setCourseName(courseRepo.findCourseById(chapterDetail.getCourseId()).get().getCourseName());
        return chapterDetail;
    }

    public Boolean getCourseFinished(Long accountId, Long chapterId) {
        Chapter chapter = getChapter(chapterId);
        Long courseId = chapter.getCourseId();
        int chapterCountByCourseId = getChapterCountByCourseId(courseId);
        int completedChapterCnt = completedChapterRepo.readCompletedChapters(accountId, courseId).size();
        return completedChapterCnt == chapterCountByCourseId;
    }

    public PutChapterFinishRes putFinishChapter(Long accountId, Long chapterId) {
        //chapterId로 다음 챕터를 갖옴
        Optional<Chapter> nextChapter = getNextChapter(chapterId);
        //chapter를 다 들었는지 확인
        Boolean courseFinished = getCourseFinished(accountId, chapterId);
        //completed chapter에 해당하는 chapter가 있는지 확인하고 있따면 그냥 다음 챕터를 리턴함
        Optional<CompletedChapter> completedChapterOptional = completedChapterRepo.readCompletedChapter(accountId, chapterId);
        if (completedChapterOptional.isPresent()) {
            if (nextChapter.isPresent())
                return new PutChapterFinishRes(courseFinished, nextChapter.get().getId());
            return new PutChapterFinishRes(courseFinished, FINISHED);
        }
        completedChapterRepo.createCompletedChapter(accountId, chapterId);
        if (nextChapter.isPresent()) {
            return new PutChapterFinishRes(courseFinished, nextChapter.get().getId());
        }
        return new PutChapterFinishRes(courseFinished, FINISHED);
    }

    public int getChapterCountByCourseId(Long courseId) {
        List<Chapter> chapters = chapterRepo.findChaptersByCourseId(courseId);
        return chapters.size();
    }
}
