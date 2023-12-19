package com.example.demo.integration.wish;

import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.utils.LoadEnvironmentVariables;
import com.example.demo.wish.dto.WishListResponseDto;
import com.example.demo.wish.dto.WishReadResponseDto;
import com.example.demo.wish.repository.WishRepository;
import com.example.demo.wish.service.WishService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
//@SpringBootTest
@LoadEnvironmentVariables
public class WishModelTest {
    @Autowired
    private WishService wishService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WishRepository wishRepository;

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-wish.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseWish {}

    @LoadTestCaseWish
    @Test
    @DisplayName("[정상 작동] toggle - 찜이 이미 존재함")
    void toggle_whenAlreadyExistedWish() {
        // given
        Long memberId = 1L;
        Long itemId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        boolean isAlreadyExisted = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertTrue(isAlreadyExisted);

        // when
        wishService.toggle(member, itemId);

        // then
        boolean isRemoved = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertFalse(isRemoved);
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[정상 작동] toggle - 찜 기록이 없음")
    void toggle_whenNonExistedWish() {
        // given
        Long memberId = 1L;
        Long itemId = 8L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        boolean isNonExisted = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertFalse(isNonExisted);

        // when
        wishService.toggle(member, itemId);

        // then
        boolean isGenerated = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertTrue(isGenerated);
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[비정상 작동] toggle - 아이템 ID가 존재하지 않음.")
    void toggle_whenNonExistedItemId() {
        // given
        Long memberId = 1L;
        Long itemId = 1000000L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        Executable func = () -> wishService.toggle(member, itemId);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[정상 작동] readWishRecord - 찜이 이미 존재함")
    void readWishRecord_whenAlreadyExistedWish() {
        // given
        Long memberId = 1L;
        Long itemId = 1L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        boolean isNonExisted = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertTrue(isNonExisted);

        // when
        ResponseEntity<WishReadResponseDto> result = wishService.readWishRecord(member, itemId);

        // then
        assertTrue(result.getBody().getIsWished());
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[정상 작동] readWishRecord - 찜 기록이 없음")
    void readWishRecord_whenNonExistedWish() {
        // given
        Long memberId = 1L;
        Long itemId = 8L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        boolean isNonExisted = wishRepository.existsByMember_IdAndItem_Id(memberId, itemId);
        assertFalse(isNonExisted);

        // when
        ResponseEntity<WishReadResponseDto> result = wishService.readWishRecord(member, itemId);

        // then
        assertFalse(result.getBody().getIsWished());
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[비정상 작동] toggle - 아이템 ID가 존재하지 않음.")
    void readWishRecord_whenNonExistedItemId() {
        // given
        Long memberId = 1L;
        Long itemId = 1000000L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        ResponseEntity<WishReadResponseDto> result = wishService.readWishRecord(member, itemId);

        // then
        assertFalse(result.getBody().getIsWished());
    }

    @LoadTestCaseWish
    @Test
    @DisplayName("[정상 작동] readMyWishLists")
    void readMyWishLists() {
        // given
        Long memberId = 2L;
        Member member = memberRepository.findById(memberId).orElseThrow();

        // when
        ResponseEntity<List<WishListResponseDto>> result = wishService.readMyWishLists(member);

        // then
        assertThat(result.getBody())
                .extracting(WishListResponseDto::getItemId)
                .hasSize(4)
                .containsAnyElementsOf(List.of(1L, 3L, 5L, 7L));
    }
}
