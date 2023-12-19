package com.example.demo.integration.review;

import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.review.dto.ReviewRequestDto;
import com.example.demo.review.dto.ReviewResponseDto;
import com.example.demo.review.entity.Review;
import com.example.demo.review.repository.ReviewRepository;
import com.example.demo.review.service.ReviewService;
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
public class ReviewModelTest {
    @Autowired
    private ReviewService reviewService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReviewRepository reviewRepository;

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

    @LoadTestCaseReview
    @Test
    @DisplayName("[정상 작동] createReview")
    void createReview() {
        // given
        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setItemId(1L);
        dto.setComment("적절한 리뷰 내용.");
        dto.setRating(1d);

        Member member = memberRepository.findById(1L).orElseThrow();

        long countBeforeCreate = reviewRepository.count();

        // when
        reviewService.createReview(dto, member);

        // then
        long countAfterCreate = reviewRepository.count();
        assertThat(countAfterCreate).isEqualTo(countBeforeCreate + 1);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[정상 작동] updateReview")
    void updateReview() {
        // given
        Long reviewId = 1L;
        String comment = "적절한 리뷰 내용.";

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setItemId(1L);
        dto.setComment(comment);
        dto.setRating(1d);

        Member member = memberRepository.findById(1L).orElseThrow();
        Review review = reviewRepository.findById(reviewId).orElseThrow();
        String commentBeforeUpdate = review.getComment();
        assertThat(commentBeforeUpdate).isNotEqualTo(comment);

        // when
        reviewService.updateReview(reviewId, dto, member);

        // then
        Review changed = reviewRepository.findById(reviewId).orElseThrow();
        String commentAfterUpdate = changed.getComment();

        assertThat(commentAfterUpdate).isEqualTo(comment);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[비정상 작동] updateReview - 존재하지 않는 리뷰")
    void updateReview_whenGivenNonExistedReviewId() {
        // given
        Long reviewId = 1000000L;
        String comment = "적절한 리뷰 내용.";

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setItemId(1L);
        dto.setComment(comment);
        dto.setRating(1d);

        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> reviewService.updateReview(reviewId, dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[비정상 작동] updateReview - 리뷰 작성자가 아닌 사람이 접근")
    void updateReview_whenAccessOtherMember() {
        // given
        Long reviewId = 1L;
        String comment = "적절한 리뷰 내용.";

        ReviewRequestDto dto = new ReviewRequestDto();
        dto.setItemId(1L);
        dto.setComment(comment);
        dto.setRating(1d);

        Member member = memberRepository.findById(5L).orElseThrow();

        // when
        Executable func = () -> reviewService.updateReview(reviewId, dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[정상 작동] deleteReview")
    void deleteReview() {
        // given
        Long reviewId = 1L;
        Member member = memberRepository.findById(1L).orElseThrow();

        long countBeforeDelete = reviewRepository.count();

        // when
        reviewService.deleteReview(reviewId, member);

        // then
        long countAfterDelete = reviewRepository.count();

        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[비정상 작동] deleteReview - 존재하지 않는 리뷰")
    void deleteReview_whenGivenNonExistedReviewId() {
        // given
        Long reviewId = 1000000L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> reviewService.deleteReview(reviewId, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[비정상 작동] updateReview - 리뷰 작성자가 아닌 사람이 접근")
    void deleteReview_whenAccessOtherMember() {
        // given
        Long reviewId = 1L;
        Member member = memberRepository.findById(5L).orElseThrow();

        // when
        Executable func = () -> reviewService.deleteReview(reviewId, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseReview
    @Test
    @DisplayName("[정상 작동] readReviewList")
    void readReviewList() {
        // given
        Long shopId = 1L;
        Pageable pageable = PageRequest.of(0, 100);

        // when
        ResponseEntity<Page<ReviewResponseDto>> result = reviewService.readReviewList(shopId, pageable);

        // then
        assertThat(result.getBody().getContent())
                .extracting(ReviewResponseDto::getId)
                .hasSize(3)
                .containsAnyElementsOf(List.of(1L, 6L, 11L));
    }
}
