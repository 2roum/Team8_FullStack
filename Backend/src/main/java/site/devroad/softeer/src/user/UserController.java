package site.devroad.softeer.src.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.devroad.softeer.exceptions.CustomException;
import site.devroad.softeer.exceptions.ExceptionType;
import site.devroad.softeer.src.user.dto.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/user/signup")
    public ResponseEntity<?> postSignUp(@RequestBody PostSignUpReq postSignUpReq) {
        if (postSignUpReq.hasNull())
            return new CustomException(ExceptionType.POST_ACCOUNT_FORM_INVALID).getResponseEntity();
        PostSignUpRes postSignUpRes = userService.join(postSignUpReq);
        return new ResponseEntity<>(postSignUpRes, HttpStatus.CREATED);
    }

    @PostMapping("/api/user/signin")
    public ResponseEntity<?> postSignIn(@RequestBody PostSignInReq postSignInReq) {
        PostSignInRes postSignInRes = userService.signIn(postSignInReq);
        return new ResponseEntity<>(postSignInRes, HttpStatus.OK);
    }

    @GetMapping("/api/user")
    public ResponseEntity<?> getUserDetail(@RequestAttribute(value = "accountId") Long accountId) {
        try {
            GetUserDetailRes getUserDetailRes = userService.getUserDetail(accountId);
            return new ResponseEntity<>(getUserDetailRes, HttpStatus.OK);
        } catch (CustomException e) {
            return e.getResponseEntity();
        }
    }

    @GetMapping("/api/user/noRoadmap")
    public ResponseEntity<?> getNoRoadmapUser(@RequestAttribute(value = "accountId") Long accountId) {
        boolean isAdmin = userService.isAdmin(accountId);
        if (isAdmin) {
            return new ResponseEntity<>(new GetNoUserRes(true, userService.getNoRoadmapUsers()), HttpStatus.OK);
        }
        return new CustomException(ExceptionType.NO_ADMIN_USER).getResponseEntity();
    }

    @GetMapping("/api/admin/users")
    public ResponseEntity<?> getAllUser(@RequestAttribute(value = "accountId") Long accountId) {
        boolean isAdmin = userService.isAdmin(accountId);
        if (!isAdmin) {
            return new CustomException(ExceptionType.NO_ADMIN_USER).getResponseEntity();
        }
        return new ResponseEntity<>(new GetAllUserRes(true, userService.getAllUser(accountId)), HttpStatus.OK);
    }
}
