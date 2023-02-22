package site.devroad.softeer.src.roadmap.course;

public class Course {
    Long id;
    Long subjectId;
    String tutorName;
    String thumbnailUrl;
    String courseName;
    String description;
    Long languageId;
    String type;

    public Course() {}

    public Course(Long id, Long subjectId, String tutorName, String thumbnailUrl, String courseName, String description, Long languageId, String type) {
        this.id = id;
        this.subjectId = subjectId;
        this.tutorName = tutorName;
        this.thumbnailUrl = thumbnailUrl;
        this.courseName = courseName;
        this.description = description;
        this.languageId = languageId;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Long languageId) {
        this.languageId = languageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
