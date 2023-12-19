package com.example.demo.integration.shop;

import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.service.ItemService;
import com.example.demo.member.dto.ShopPageMemberResponseDto;
import com.example.demo.member.service.MemberService;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

//@SpringBootTest
@LoadEnvironmentVariables
public class ShopModelTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private MemberService memberService;

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-shop.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseShop {}

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-follow.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseFollow {}

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-review.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseReview {}

    @LoadTestCaseShop
    @Test
    @DisplayName("[정상 작동] readItemsOfShop")
    void readItemsOfShop() {
        // given
        Long shopId = 1L;
        Long[] exclude = {};
        Pageable pageable = PageRequest.of(0, 10);

        // when
        ResponseEntity<Page<ItemSearchResponseDto>> result = itemService.readItemsOfShop(shopId, exclude, pageable);

        // then
        assertThat(result.getBody().getContent())
                .hasSize(4)
                .extracting(ItemSearchResponseDto::getItemId)
                .isEqualTo(List.of(1L, 3L, 5L, 7L));
    }

    @LoadTestCaseShop
    @Test
    @DisplayName("[비정상 작동] readItemsOfShop - 존재하지 않는 shopId")
    void readItemsOfShop_whenGivenNonExistedShopId() {
        // given
        Long shopId = 100000L;
        Long[] exclude = {};
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Executable func = () -> itemService.readItemsOfShop(shopId, exclude, pageable);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readShopPage")
    void readShopPage() {
        // given
        Long shopId = 3L;

        // when
        ResponseEntity<ShopPageMemberResponseDto> result = memberService.readShopPage(shopId);

        // then
        assertThat(result.getBody())
                .extracting(ShopPageMemberResponseDto::getFollowers)
                .isEqualTo(2L);
        assertThat(result.getBody())
                .extracting(ShopPageMemberResponseDto::getFollowings)
                .isEqualTo(1L);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[비정상 작동] readShopPage - 존재하지 않는 상점 ID")
    void readShopPage_whenGivenNonExistedShopId() {
        // given
        Long shopId = 100000L;

        // when
        Executable func = () -> memberService.readShopPage(shopId);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[정상 작동] readShopPage - 리뷰 평균이 적절히 계산되는지 확인.")
    void readShopPage_testForReviewRating() {
        // given
        Long shopId = 1L;

        // when
        ResponseEntity<ShopPageMemberResponseDto> result = memberService.readShopPage(shopId);

        // then
        assertThat(result.getBody())
                .extracting(ShopPageMemberResponseDto::getRatingAvg)
                .isEqualTo(7d/3);
    }
}
