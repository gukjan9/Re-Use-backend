package com.example.demo.integration.review;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.review.controller.ReviewController;
import com.example.demo.review.dto.ReviewRequestDto;
import com.example.demo.review.dto.ReviewResponseDto;
import com.example.demo.review.service.ReviewService;
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
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class ReviewAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private ReviewController reviewController;
    @MockBean
    private ReviewService reviewService;

    @Autowired
    private AuthTestUtil authTestUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] POST /api/reviews")
    void createReview() throws Exception {
        // given
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setItemId(1L);
        dto.setComment("적절한 리뷰 내용.");

        MockHttpServletRequestBuilder request = post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.createReview(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[비정상 작동] POST /api/reviews - JWT 없이 호출")
    void createReview_withoutJwt() throws Exception {
        // given
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setComment("적절한 리뷰 내용.");

        MockHttpServletRequestBuilder request = post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.createReview(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] PUT /api/reviews/{review_id}")
    void updateReview() throws Exception {
        // given
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setComment("적절한 리뷰 내용.");

        MockHttpServletRequestBuilder request = put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.updateReview(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[비정상 작동] PUT /api/reviews - JWT 없이 호출")
    void updateReview_withoutJwt() throws Exception {
        // given
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setComment("적절한 리뷰 내용.");

        MockHttpServletRequestBuilder request = put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.updateReview(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] DELETE /api/reviews/{review_id}")
    void deleteReview() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/reviews/1");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.deleteReview(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[비정상 작동] PUT /api/reviews - JWT 없이 호출")
    void deleteReview_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = delete("/api/reviews/1");

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(reviewService.deleteReview(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/shop/{shopId}/reviews")
    void readReviewList() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shop/1/reviews")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<ReviewResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(reviewService.readReviewList(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }
}
