package com.example.demo.integration.shop;

import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.service.ItemService;
import com.example.demo.member.dto.ShopPageMemberResponseDto;
import com.example.demo.member.service.MemberService;
import com.example.demo.shop.controller.ShopController;
import com.example.demo.utils.LoadEnvironmentVariables;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class ShopAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private ShopController shopController;
    @MockBean
    private ItemService itemService;
    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("[정상 작동] GET /api/shops/{shopId}/items")
    void readItemsOfShop_whenChangeNickname() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1/items")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<ItemSearchResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(itemService.readItemsOfShop(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/shops/{shopId}")
    void readShopPage() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/shops/1");

        ResponseEntity<ShopPageMemberResponseDto> result = ResponseEntity.ok(new ShopPageMemberResponseDto());
        when(memberService.readShopPage(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }
}
