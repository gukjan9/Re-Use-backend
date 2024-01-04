package com.example.demo.integration.location;

import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.service.ItemService;
import com.example.demo.location.controller.LocationController;
import com.example.demo.location.service.LocationService;
import com.example.demo.utils.LoadEnvironmentVariables;
import com.example.demo.utils.testcase.AuthTestUtil;
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
public class LocationAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private LocationController locationController;
    @MockBean
    private LocationService locationService;
    @MockBean
    private ItemService itemService;

    @Autowired
    private AuthTestUtil authTestUtil;

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/nearby-items")
    void read_run() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/nearby-items")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<Page<ItemSearchResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(itemService.readNearbyItems(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[비정상 작동] GET /api/nearby-items - jwt 없이 호출")
    void read_run_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/nearby-items")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<ItemSearchResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(itemService.readNearbyItems(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
