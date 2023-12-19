package com.example.demo.integration.wish;

import com.example.demo.item.service.ItemService;
import com.example.demo.utils.LoadEnvironmentVariables;
import com.example.demo.utils.testcase.AuthTestUtil;
import com.example.demo.wish.controller.WishController;
import com.example.demo.wish.dto.TopItemResponseDto;
import com.example.demo.wish.dto.WishListResponseDto;
import com.example.demo.wish.service.WishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class WishAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private WishController wishController;
    @MockBean
    private WishService wishService;
    @MockBean
    private ItemService itemService;

    @Autowired
    private AuthTestUtil authTestUtil;

    @Test
    @DisplayName("[정상 작동] GET /api/top-items")
    void readPopularItems_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/top-items")
                .param("state", "SELLING")
                .param("state", "RESERVED")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<TopItemResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(itemService.readPopularItems(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] POST /api/items/{itemId}/wishes")
    void toggleWish() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/api/items/1/wishes");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<Void> result = ResponseEntity.ok(null);
        when(wishService.toggle(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] POST /api/items/{itemId}/wishes - JWT 없이 호출")
    void toggleWish_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/api/items/1/wishes");

        ResponseEntity<Void> result = ResponseEntity.ok(null);
        when(wishService.toggle(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/items/{itemId}/wishes")
    void readWishRecord() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/items/1/wishes");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<Void> result = ResponseEntity.ok(null);
        when(wishService.toggle(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] GET /api/items/{itemId}/wishes - JWT 없이 호출")
    void readWishRecord_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/items/1/wishes");

        ResponseEntity<Void> result = ResponseEntity.ok(null);
        when(wishService.toggle(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/mypages/wishlists")
    void readMyWishLists() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/wishlists");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<List<WishListResponseDto>> result = ResponseEntity.ok(List.of());
        when(wishService.readMyWishLists(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] GET /api/mypages/wishlists - JWT 없이 호출")
    void readMyWishLists_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/wishlists");

        ResponseEntity<List<WishListResponseDto>> result = ResponseEntity.ok(List.of());
        when(wishService.readMyWishLists(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
