package site.devroad.softeer.src.roadmap.dto.domain;

import site.devroad.softeer.src.roadmap.chapter.Chapter;

public class ChapterDetail {
    private Long courseId;
    private Long chapterId;

    private String courseName;

    private String chapterName;
    private String chapterUrl;
    private String thumbnailUrl;
    private String explain;
    private Boolean finish;

    public ChapterDetail(Long courseId, Long chapterId, String courseName, String chapterName, String chapterUrl, String thumbnailUrl, String explain, Boolean finish) {
        this.courseId = courseId;
        this.chapterId = chapterId;
        this.courseName = courseName;
        this.chapterName = chapterName;
        this.chapterUrl = chapterUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.explain = explain;
        this.finish = finish;
    }

    public ChapterDetail(Chapter chapter, String courseName) {
        this.courseId = chapter.getCourseId();
        this.chapterId = chapter.getId();
        this.courseName = courseName;
        this.chapterName = chapter.getTitle();
        this.chapterUrl = chapter.getChapterUrl();
        this.thumbnailUrl = chapter.getThumbnailUrl();
        this.explain = chapter.getDescription();
        this.finish = false;
    }

    public void setCourseName(String courseName){
        this.courseName = courseName;
    }

    public Long getCourseId(){
        return courseId;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getExplain() {
        return explain;
    }

    public Boolean getFinish() {
        return finish;
    }

    @Override
    public String toString() {
        return "ChapterDetail{" +
                "courseId=" + courseId +
                ", chapterId=" + chapterId +
                ", courseName='" + courseName + '\'' +
                ", chapterName='" + chapterName + '\'' +
                ", chapterUrl='" + chapterUrl + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", explain='" + explain + '\'' +
                ", finish=" + finish +
                '}';
    }
}
