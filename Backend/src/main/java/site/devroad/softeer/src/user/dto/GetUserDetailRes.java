package site.devroad.softeer.src.user.dto;

public class GetUserDetailRes {
    private boolean success;
    private Long userId;
    private String userName;
    private Long roadmapId;
    private Long totalSubjectIdx;
    private Long curSubjectIdx;
    private Float chapterPercent;
    private Long curChapterPK;
    private Boolean subscribe;
    private Boolean roadmapStarted;
    private String curChapterName = "없는 상태";

    private GetUserDetailRes(Long userId, String userName, Boolean subscribe) {
        this.subscribe = subscribe;
        this.success = true;
        this.userId = userId;
        this.userName = userName;
        this.roadmapId = -1L;
        this.totalSubjectIdx = 0L;
        this.curSubjectIdx = 0L;
        this.chapterPercent = 0F;
        this.curChapterPK = -1L;
        this.roadmapStarted = false;
    }

    private GetUserDetailRes(Long userId, Long roadmapId, Long totalSubjectIdx, String userName, Boolean subscribe) {
        this.subscribe = subscribe;
        this.success = true;
        this.userId = userId;
        this.userName = userName;
        this.roadmapId = roadmapId;
        this.totalSubjectIdx = totalSubjectIdx;
        this.curSubjectIdx = 0L;
        this.chapterPercent = 0F;
        this.curChapterPK = -1L;
        this.roadmapStarted = false;
    }

    private GetUserDetailRes() {
        this.success = true;
        this.roadmapStarted = true;
    }

    public static GetUserDetailRes createNoRoadmapUserDetail(Long userId, String userName, Boolean subscribe) {
        return new GetUserDetailRes(userId, userName, subscribe);
    }

    public static GetUserDetailRes createNotStartUserDetail(Long userId, Long roadmapId, Long totalSubjectIdx, String userName, Boolean subscribe) {
        return new GetUserDetailRes(userId, roadmapId, totalSubjectIdx, userName, subscribe);
    }

    public static GetUserDetailRes createUserDetail() {
        return new GetUserDetailRes();
    }

    public String getCurChapterName() {
        return curChapterName;
    }

    public void setCurChapterName(String curChapterName) {
        this.curChapterName = curChapterName;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public boolean isSuccess() {
        return success;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(Long roadmapId) {
        this.roadmapId = roadmapId;
    }

    public Long getTotalSubjectIdx() {
        return totalSubjectIdx;
    }

    public void setTotalSubjectIdx(Long totalSubjectIdx) {
        this.totalSubjectIdx = totalSubjectIdx;
    }

    public Long getCurSubjectIdx() {
        return curSubjectIdx;
    }

    public void setCurSubjectIdx(Long curSubjectIdx) {
        this.curSubjectIdx = curSubjectIdx;
    }

    public Float getChapterPercent() {
        return chapterPercent;
    }

    public void setChapterPercent(Float chapterPercent) {
        this.chapterPercent = chapterPercent;
    }

    public Long getCurChapterPK() {
        return curChapterPK;
    }

    public void setCurChapterPK(Long curChapterPK) {
        this.curChapterPK = curChapterPK;
    }
}
