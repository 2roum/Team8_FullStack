package site.devroad.softeer.src.user.dto;

import site.devroad.softeer.src.user.dto.domain.UserDetail;

import java.util.List;

public class GetAllUserRes {
    private boolean success;
    private List<UserDetail> userDetailList;
    public GetAllUserRes(List<UserDetail> userDetailList) {
        this.success = true;
        this.userDetailList = userDetailList;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<UserDetail> getUserDetailList() {
        return userDetailList;
    }
}
