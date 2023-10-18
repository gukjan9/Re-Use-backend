package com.example.demo.unit.item;

import com.amazonaws.util.IOUtils;
import com.example.demo.item.controller.ItemController;
import com.example.demo.item.dto.itemRequestDto;
import com.example.demo.item.service.ItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemService itemService;
    @InjectMocks
    private ItemController itemController;

    @Autowired
    private ObjectMapper objectMapper;

//    @WithMockPrincipal
    @Test
    @DisplayName("[정상 작동] POST /api/items")
    void createItem() throws Exception {
        // given

        // API에서 요구하는 것들.
        MockMultipartFile mainImage = null;
        List<MockMultipartFile> subImage = null;
        itemRequestDto dto = null;

        // Http 요청 구성하기.
        MockHttpServletRequestBuilder requestBuilder = post("/api/items");

        // Stub 구성하기. - Mock 인스턴스가 특정 행동을 하길 바랄 때, 사용함.

        // when & then
        mvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk());
    }

    private MockMultipartFile getMockMultipartFile(String path, String key) throws IOException {
        File imageFile = new File(path);

        return new MockMultipartFile(key, IOUtils.toByteArray(new FileInputStream(imageFile)));
    }
}
