package com.example.demo.integration.member;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.member.controller.MemberController;
import com.example.demo.member.dto.LocationRequestDto;
import com.example.demo.member.dto.MemberInfoRequestDto;
import com.example.demo.member.dto.ShopPageMemberResponseDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.utils.LoadEnvironmentVariables;
import com.example.demo.utils.testcase.AuthTestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.net.URL;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class MemberAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private MemberController memberController;
    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthTestUtil authTestUtil;

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] PUT /api/auth/members/me - JWT 없이 호출")
    void updateMember_withoutJwt() throws Exception {
        // given
        MemberInfoRequestDto dto = new MemberInfoRequestDto();

        MockHttpServletRequestBuilder request = put("/api/auth/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateMember(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] PUT /api/auth/members/me - nickname 변경")
    void updateMember_whenChangeNickname() throws Exception {
        // given
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setNickname("mock nickname");

        MockHttpServletRequestBuilder request = put("/api/auth/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateMember(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] PUT /api/auth/members/me - shopName 변경")
    void updateMember_whenChangeShopName() throws Exception {
        // given
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setShopName("mock shopName");

        MockHttpServletRequestBuilder request = put("/api/auth/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateMember(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] PUT /api/auth/members/me/locations - JWT 없이 호출")
    void updateMemberLocations_without_Jwt() throws Exception {
        // given
        LocationRequestDto dto = new LocationRequestDto();
        dto.setLocation("카카오 본사");

        MockHttpServletRequestBuilder request = put("/api/auth/members/me/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateMemberLocation(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] PUT /api/auth/members/me/locations")
    void updateMemberLocations() throws Exception {
        // given
        LocationRequestDto dto = new LocationRequestDto();
        dto.setLocation("카카오 본사");

        MockHttpServletRequestBuilder request = put("/api/auth/members/me/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateMemberLocation(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] POST /api/auth/members/me/images")
    void updateProfileImage() throws Exception {
        // given
        URL url = new URL("https://cdn.pixabay.com/photo/2023/09/20/11/40/plants-8264654_1280.jpg");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", url.openStream());

        MockHttpServletRequestBuilder request = multipart("/api/auth/members/me/images")
                .file(mockMultipartFile);
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateProfileImage(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] POST /api/auth/members/me/images - 잘못된 image Key Name")
    void updateProfileImage_withWrongKeyNameOfImage() throws Exception {
        // given
        URL url = new URL("https://cdn.pixabay.com/photo/2023/09/20/11/40/plants-8264654_1280.jpg");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("wrong_image_key_name", url.openStream());

        MockHttpServletRequestBuilder request = multipart("/api/auth/members/me/images")
                .file(mockMultipartFile);
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.updateProfileImage(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/auth/members/{memberId}")
    void readMemberInShopPage() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/auth/members/1");

        ResponseEntity<ShopPageMemberResponseDto> result = ResponseEntity.ok(new ShopPageMemberResponseDto());
        when(memberService.readMemberInShopPage(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] DELETE /api/auth/members/me")
    void deleteMember() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/auth/members/me");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.deleteMember(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] DELETE /api/auth/members/me - JWT 없이 호출")
    void deleteMember_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/auth/members/me");

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(memberService.deleteMember(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
