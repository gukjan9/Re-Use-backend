package com.example.demo.integration.trade;

import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.entity.Item;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.trade.dto.MyOrdersResponseDto;
import com.example.demo.trade.dto.MySalesResponseDto;
import com.example.demo.trade.dto.TradeRequestDto;
import com.example.demo.trade.entity.Trade;
import com.example.demo.trade.repository.TradeRepository;
import com.example.demo.trade.service.TradeService;
import com.example.demo.trade.type.State;
import com.example.demo.utils.LoadEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.file.AccessDeniedException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@SpringBootTest
@LoadEnvironmentVariables
public class TradeModelTest {
    @Autowired
    private TradeService tradeService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TradeRepository tradeRepository;

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-trade.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseTrade {}

    @LoadTestCaseTrade
    @Test
    @DisplayName("[정상 작동] readOrders")
    void readOrders() {
        // given
        Member member = memberRepository.findById(1L).orElseThrow();
        State[] states = {};
        Pageable pageable = PageRequest.of(0, 10);

        // when
        ResponseEntity<Page<MyOrdersResponseDto>> result = tradeService.readOrders(member, states, pageable);

        // then
        assertThat(result.getBody().getContent())
                .extracting(MyOrdersResponseDto::getItemId)
                .containsAnyElementsOf(List.of(2L, 3L, 4L));
    }

    @LoadTestCaseTrade
    @Test
    @DisplayName("[정상 작동] readSales")
    void readSales() {
        // given
        Member member = memberRepository.findById(1L).orElseThrow();
        State[] states = {};
        Pageable pageable = PageRequest.of(0, 10);

        // when
        ResponseEntity<Page<MySalesResponseDto>> result = tradeService.readSales(member, states, pageable);

        // then
        assertThat(result.getBody().getContent())
                .extracting(MySalesResponseDto::getItemId)
                .containsAnyElementsOf(List.of(1L, 2L));
    }

    @LoadTestCaseTrade
    @Test
    @DisplayName("[정상 작동] updateTradeRecord - 기존 거래 기록이 없는 경우")
    void updateTradeRecord_whenNonExistedTradeRecord() throws AccessDeniedException {
        // given
        Member memberWhoOwnItem = memberRepository.findById(4L).orElseThrow();
        Long itemId = 8L;
        TradeRequestDto dto = new TradeRequestDto(1L, State.SOLDOUT);

        long countBeforeUpdate = tradeRepository.count();

        // when
        tradeService.updateTradeRecord(memberWhoOwnItem, dto, itemId);

        // then
        long countAfterUpdate = tradeRepository.count();

        assertThat(countAfterUpdate).isEqualTo(countBeforeUpdate + 1);
    }

    @Transactional
    @LoadTestCaseTrade
    @Test
    @DisplayName("[정상 작동] updateTradeRecord - 기존 거래 기록이 존재하는 경우")
    void updateTradeRecord_whenExistedTradeRecord() throws AccessDeniedException {
        // given
        State state = State.SOLDOUT;
        Member memberWhoOwnItem = memberRepository.findById(1L).orElseThrow();
        Long itemId = 1L;
        TradeRequestDto dto = new TradeRequestDto(2L, state);

        Long tradeId = 1L;

        Trade tradeBeforeUpdate = tradeRepository.findById(tradeId).orElseThrow();
        assertThat(tradeBeforeUpdate.getItem())
                .extracting(Item::getState)
                .isNotEqualTo(state);

        // when
        tradeService.updateTradeRecord(memberWhoOwnItem, dto, itemId);

        // then
        Trade tradeAfterUpdate = tradeRepository.findById(tradeId).orElseThrow();
        assertThat(tradeAfterUpdate.getItem())
                .extracting(Item::getState)
                .isNotNull();
    }

    @LoadTestCaseTrade
    @Test
    @DisplayName("[비정상 작동] updateTradeRecord - 상품 소유자가 아닌 사람의 접근")
    void updateTradeRecord_whenAccessNonGrantedPerson() {
        // given
        Member memberWhoNotOwnItem = memberRepository.findById(1L).orElseThrow();
        Long itemId = 8L;
        TradeRequestDto dto = new TradeRequestDto(1L, State.SOLDOUT);

        // when
        Executable func = () -> tradeService.updateTradeRecord(memberWhoNotOwnItem, dto, itemId);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseTrade
    @Test
    @DisplayName("[비정상 작동] updateTradeRecord - 존재하지 않는 상품 ID")
    void updateTradeRecord_whenNonExistedItemId() {
        // given
        Member memberWhoOwnItem = memberRepository.findById(4L).orElseThrow();
        Long itemId = 10000000L;
        TradeRequestDto dto = new TradeRequestDto(1L, State.SOLDOUT);

        // when
        Executable func = () -> tradeService.updateTradeRecord(memberWhoOwnItem, dto, itemId);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseTrade
    @Test
    @DisplayName("[비정상 작동] updateTradeRecord - 존재하지 않는 구매자 ID")
    void updateTradeRecord_whenNonExistedConsumerId() {
        // given
        Member memberWhoOwnItem = memberRepository.findById(4L).orElseThrow();
        Long itemId = 8L;
        TradeRequestDto dto = new TradeRequestDto(10000000L, State.SOLDOUT);

        // when
        Executable func = () -> tradeService.updateTradeRecord(memberWhoOwnItem, dto, itemId);

        // then
        assertThrows(Throwable.class, func);
    }
}
