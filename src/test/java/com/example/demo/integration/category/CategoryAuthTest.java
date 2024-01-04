package com.example.demo.integration.category;

import com.example.demo.category.controller.CategoryController;
import com.example.demo.category.dto.CategoryResponseDto;
import com.example.demo.category.dto.ItemInCategoryResponseDto;
import com.example.demo.category.service.CategoryService;
import com.example.demo.utils.LoadEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class CategoryAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private CategoryController categoryController;
    @MockBean
    private CategoryService categoryService;

    @Test
    @DisplayName("[정상 작동] GET /api/categories/{id}")
    void read_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/1")
                .param("layer", "1");

        ResponseEntity<CategoryResponseDto> result = ResponseEntity.ok(new CategoryResponseDto());
        when(categoryService.readCategory(any(), anyInt()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories/{id} - layer 없이도 동작")
    void read_run_without_layer() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/1");

        ResponseEntity<CategoryResponseDto> result = ResponseEntity.ok(new CategoryResponseDto());
        when(categoryService.readCategory(any(), anyInt()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories/{id}/categories")
    void readChildCategory_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/1/categories");

        ResponseEntity<List<CategoryResponseDto>> result = ResponseEntity.ok(List.of(new CategoryResponseDto()));
        when(categoryService.readChildCategory(any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories/{id}/items")
    void readChildItem_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/1/items")
                .param("layer", "1");

        List<ItemInCategoryResponseDto> dtoList = List.of(new ItemInCategoryResponseDto());
        Page<ItemInCategoryResponseDto> dtoPage = new PageImpl<>(dtoList);
        ResponseEntity<Page<ItemInCategoryResponseDto>> result = ResponseEntity.ok(dtoPage);
        when(categoryService.readChildItem(any(), anyInt(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories/{id}/items - layer 없이도 동작")
    void readChildItem_run_without_layer() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/1/items");

        List<ItemInCategoryResponseDto> dtoList = List.of(new ItemInCategoryResponseDto());
        Page<ItemInCategoryResponseDto> dtoPage = new PageImpl<>(dtoList);
        ResponseEntity<Page<ItemInCategoryResponseDto>> result = ResponseEntity.ok(dtoPage);
        when(categoryService.readChildItem(any(), anyInt(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories")
    void readCategoryLarge_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories");

        ResponseEntity<List<CategoryResponseDto>> result = ResponseEntity.ok(List.of(new CategoryResponseDto()));
        when(categoryService.readItemsLarge())
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[정상 작동] GET /api/categories/all/categories")
    void readCategoryRecursively_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/categories/all/categories");

        ResponseEntity<List<CategoryResponseDto>> result = ResponseEntity.ok(List.of(new CategoryResponseDto()));
        when(categoryService.readItemsLarge())
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }
}
