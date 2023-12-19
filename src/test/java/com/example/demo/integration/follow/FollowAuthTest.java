package com.example.demo.integration.follow;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.follow.controller.FollowController;
import com.example.demo.follow.dto.FollowMemberResponseDto;
import com.example.demo.follow.dto.FollowResponseDto;
import com.example.demo.follow.dto.FollowersResponseDto;
import com.example.demo.follow.service.FollowService;
import com.example.demo.utils.LoadEnvironmentVariables;
import com.example.demo.utils.testcase.AuthTestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class FollowAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private FollowController followController;
    @MockBean
    private FollowService followService;

    @Autowired
    private AuthTestUtil authTestUtil;

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] POST /api/shops/{shopId}/follows")
    void toggleShopFollow() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/api/shops/1/follows");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<FollowResponseDto> result = ResponseEntity.ok(new FollowResponseDto(true));
        when(followService.toggleShopFollow(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] POST /api/shops/{shopId}/follows - JWT 없이 호출")
    void toggleShopFollow_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/api/shops/1/follows");

        ResponseEntity<FollowResponseDto> result = ResponseEntity.ok(new FollowResponseDto(true));
        when(followService.toggleShopFollow(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/shops/{shopId}/follows")
    void readFollowersByMemberId() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1/follows");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<FollowResponseDto> result = ResponseEntity.ok(new FollowResponseDto(true));
        when(followService.readFollowRecordAboutTarget(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] GET /api/shops/{shopId}/follows - JWT 없이 호출")
    void readFollowersByMemberId_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1/follows");

        ResponseEntity<FollowResponseDto> result = ResponseEntity.ok(new FollowResponseDto(true));
        when(followService.readFollowRecordAboutTarget(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/shops/{shopId}/followings")
    void readFollowingsByMemberId() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1/followings");

        ResponseEntity<List<FollowMemberResponseDto>> result = ResponseEntity.ok(List.of());
        when(followService.readFollowingsByShopId(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/shops/{shopId}/followers")
    void readFollowerListByShopId() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1/followers");

        ResponseEntity<List<FollowersResponseDto>> result = ResponseEntity.ok(List.of());
        when(followService.readFollowersByShopId(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }@AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] DELETE /api/follows/{followId}")
    void deleteFollow() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/follows/1");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(followService.deleteFollowRecord(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] DELETE /api/follows/{followId} - JWT 없이 호출")
    void deleteFollow_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/follows/1");

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(followService.deleteFollowRecord(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
