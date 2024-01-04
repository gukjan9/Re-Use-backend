package com.example.demo.integration.trade;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.trade.controller.TradeController;
import com.example.demo.trade.dto.MyOrdersResponseDto;
import com.example.demo.trade.dto.TradeRequestDto;
import com.example.demo.trade.service.TradeService;
import com.example.demo.trade.type.State;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@AutoConfigureMockMvc
@LoadEnvironmentVariables
public class TradeAuthTest {
    @Autowired
    private MockMvc mvc;

    @InjectMocks
    private TradeController tradeController;
    @MockBean
    private TradeService tradeService;

    @Autowired
    private AuthTestUtil authTestUtil;
    @Autowired
    private ObjectMapper objectMapper;

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/mypages/orders")
    void readMyPageOrders() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/orders")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<Page<MyOrdersResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(tradeService.readOrders(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] GET /api/mypages/orders - JWT 없이 호출")
    void readMyPageOrders_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/orders")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<MyOrdersResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(tradeService.readOrders(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] GET /api/mypages/sales")
    void readMyPageSales() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/sales")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<Page<MyOrdersResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(tradeService.readOrders(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] GET /api/mypages/sales - JWT 없이 호출")
    void readMyPageSales_withoutJwt() throws Exception {
        // given
        MockHttpServletRequestBuilder request = get("/api/mypages/sales")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc");

        ResponseEntity<Page<MyOrdersResponseDto>> result = ResponseEntity.ok(Page.empty());
        when(tradeService.readOrders(any(), any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[정상 작동] POST /api/trades")
    void updateTradeRecord() throws Exception {
        // given
        Long itemId = 1L;
        Long memberId = 1L;
        TradeRequestDto dto = new TradeRequestDto(memberId, State.SOLDOUT);

        MockHttpServletRequestBuilder request = post("/api/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));
        request = authTestUtil.setAccessToken(request);

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(tradeService.updateItemState(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @AuthTestUtil.LoadTestCaseAuth
    @Test
    @DisplayName("[비정상 작동] POST /api/trades - JWT 없이 호출")
    void updateTradeRecord_withoutJwt() throws Exception {
        // given
        Long itemId = 1L;
        Long memberId = 1L;
        TradeRequestDto dto = new TradeRequestDto(memberId, State.SOLDOUT);

        MockHttpServletRequestBuilder request = post("/api/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        ResponseEntity<MessageResponseDto> result = ResponseEntity.ok(new MessageResponseDto("mock msg", 200));
        when(tradeService.updateItemState(any(), any()))
                .thenReturn(result);

        // when & then
        mvc.perform(request)
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
