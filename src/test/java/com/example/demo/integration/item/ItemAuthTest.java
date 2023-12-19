package com.example.demo.integration.item;

import com.example.demo.item.controller.ItemController;
import com.example.demo.item.dto.ItemResponseDto;
import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.entity.Item;
import com.example.demo.item.service.ItemService;
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
public class ItemAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private ItemController itemController;
    @MockBean
    private ItemService itemService;

    @Test
    @DisplayName("[정상 작동] GET /api/items?keyword=<검색어>&page=0&size=4&sort=createdAt,desc")
    void read_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/items")
                .param("keyword", "shirt")
                .param("state", "SOLDOUT")
                .param("state", "RESERVED")
                .param("state", "SELLING")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<ItemSearchResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(itemService.searchItem(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/items/{itemId}")
    void showItem_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/items/1");

       ItemResponseDto result = new ItemResponseDto();
        when(itemService.showItem(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }
}
