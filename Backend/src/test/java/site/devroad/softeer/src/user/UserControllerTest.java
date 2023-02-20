package site.devroad.softeer.src.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import site.devroad.softeer.src.user.dto.*;
import site.devroad.softeer.utility.JwtUtility;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {
    @MockBean
    JwtUtility jwtUtility;
    @MockBean
    UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("회원가입 테스트")
    void 회원가입_테스트() throws Exception {
        //given
        given(userService.join(any(PostSignUpReq.class))).willReturn(
                new PostSignUpRes(1000L)
        );

        //when
        mockMvc.perform(
                        post("/api/user/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"signupTest@gmail.com\"," +
                                        "\"password\":\"1234\"," +
                                        "\"name\":\"signupTest\"," +
                                        "\"phone\":\"01042427272\"}")
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.userId").exists())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행 여부를 체크
        verify(userService).join(any(PostSignUpReq.class));
    }

    @Test
    @DisplayName("로그인 테스트")
    void 로그인_테스트() throws Exception {
        //given
        given(userService.signIn(any(PostSignInReq.class))).willReturn(
                new PostSignInRes("test-jwt", false)
        );
        //when
        mockMvc.perform(
                        post("/api/user/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"signupTest@gmail.com\"," +
                                        "\"password\":\"1234\"}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").exists())
                .andExpect(jsonPath("$.jwt").exists())
                .andExpect(jsonPath("$.admin").exists())
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행 여부를 체크
        verify(userService).signIn(any(PostSignInReq.class));
    }

    @Test
    @DisplayName("로드맵이 없는 유저 정보 받아오기")
    void 로드맵이없는_유저_정보_받아오기_테스트() throws Exception {
        //given
        GetUserDetailRes testDto = GetUserDetailRes.createNoRoadmapUserDetail(1000L, "testName", false);
        given(userService.getUserDetail(any(Long.class))).willReturn(testDto);
        given(jwtUtility.getAccountId("testJwt")).willReturn(1000L);
        String expectedJson = new ObjectMapper().writeValueAsString(testDto);

        //when
        mockMvc.perform(
                        get("/api/user")
                                .header("jwt", "testJwt")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson))
                .andDo(print());

        // verify : 해당 객체의 메소드가 실행 여부를 체크
        verify(userService).getUserDetail(any(Long.class));
    }
}