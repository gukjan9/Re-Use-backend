package com.example.demo.integration.follow;

import com.example.demo.follow.dto.FollowMemberResponseDto;
import com.example.demo.follow.dto.FollowResponseDto;
import com.example.demo.follow.dto.FollowersResponseDto;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.follow.service.FollowService;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.utils.LoadEnvironmentVariables;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

//@SpringBootTest
@LoadEnvironmentVariables
public class FollowModelTest {
    @Autowired
    private FollowService followService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private FollowRepository followRepository;

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

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] toggleShopFollow - 팔로우 기록이 없을 경우")
    void toggleShopFollow_whenNonExistedFollowRecord() {
        // given
        Long shopId = 2L;
        Member member = memberRepository.findById(1L).orElseThrow();

        long countBeforeFollow = followRepository.count();

        // when
        ResponseEntity<FollowResponseDto> result = followService.toggleShopFollow(member, shopId);

        // then
        long countAfterFollow = followRepository.count();
        assertThat(countAfterFollow).isEqualTo(countBeforeFollow + 1);

        assertThat(result.getBody())
                .extracting(FollowResponseDto::getIsFollow)
                .isEqualTo(true);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] toggleShopFollow - 팔로우 기록이 있을 경우")
    void toggleShopFollow_whenExistedFollowRecord() {
        // given
        Long shopId = 3L;
        Member member = memberRepository.findById(1L).orElseThrow();

        long countBeforeFollow = followRepository.count();

        // when
        ResponseEntity<FollowResponseDto> result = followService.toggleShopFollow(member, shopId);

        // then
        long countAfterFollow = followRepository.count();
        assertThat(countAfterFollow).isEqualTo(countBeforeFollow - 1);

        assertThat(result.getBody())
                .extracting(FollowResponseDto::getIsFollow)
                .isEqualTo(false);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[비정상 작동] toggleShopFollow - 존재하지 않는 상점 ID")
    void toggleShopFollow_whenNonExistedShopId() {
        // given
        Long shopId = 10000000L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> followService.toggleShopFollow(member, shopId);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowRecordAboutTarget - 팔로우 기록이 없을 경우")
    void readFollowRecordAboutTarget_whenNonExistedFollowRecord() {
        // given
        Long shopId = 2L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        ResponseEntity<FollowResponseDto> result = followService.readFollowRecordAboutTarget(member, shopId);

        // then
        assertThat(result.getBody())
                .extracting(FollowResponseDto::getIsFollow)
                .isEqualTo(false);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowRecordAboutTarget - 팔로우 기록이 있을 경우")
    void readFollowRecordAboutTarget_whenExistedFollowRecord() {
        // given
        Long shopId = 3L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        ResponseEntity<FollowResponseDto> result = followService.readFollowRecordAboutTarget(member, shopId);

        // then
        assertThat(result.getBody())
                .extracting(FollowResponseDto::getIsFollow)
                .isEqualTo(true);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowRecordAboutTarget - 존재하지 않는 상점 ID")
    void readFollowRecordAboutTarget_whenNonExistedShopId() {
        // given
        Long shopId = 10000000L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        ResponseEntity<FollowResponseDto> result = followService.readFollowRecordAboutTarget(member, shopId);

        // then
        assertThat(result.getBody())
                .extracting(FollowResponseDto::getIsFollow)
                .isEqualTo(false);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowersByShopId")
    void readFollowersByShopId() {
        // given
        Long shopId = 3L;

        // when
        ResponseEntity<List<FollowersResponseDto>> result = followService.readFollowersByShopId(shopId, null);

        // then
        assertThat(result.getBody())
                .extracting(FollowersResponseDto::getId)
                .containsAnyElementsOf(List.of(1L, 2L));
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowersByShopId - JWT가 주어진 경우, 각 팔로워를 팔로우하는지 여부를 알려줌.")
    void readFollowersByShopId_withJwt() {
        // given
        Long shopId = 3L;
        Member memberLoggedIn = memberRepository.findById(2L).orElseThrow();

        // when
        ResponseEntity<List<FollowersResponseDto>> result = followService.readFollowersByShopId(shopId, memberLoggedIn);

        // then
        assertThat(result.getBody())
                .extracting(FollowersResponseDto::getIsPrincipalFollowing)
                .isEqualTo(List.of(true, false));
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] readFollowingsByShopId")
    void readFollowingsByShopId() {
        // given
        Long shopId = 3L;

        // when
        ResponseEntity<List<FollowMemberResponseDto>> result = followService.readFollowingsByShopId(shopId);

        // then
        assertThat(result.getBody())
                .extracting(FollowMemberResponseDto::getId)
                .containsAnyElementsOf(List.of(4L));
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] deleteFollowRecord - 팔로우 당한 당사자가 삭제 요청")
    void deleteFollowRecord() {
        // given
        Long followId = 1L;
        Member member = memberRepository.findById(3L).orElseThrow();

        long countBeforeDelete = followRepository.count();

        // when
        followService.deleteFollowRecord(followId, member);

        // then
        long countAfterDelete = followRepository.count();

        assertThat(countAfterDelete).isEqualTo(countBeforeDelete - 1);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] deleteFollowRecord - 팔로우 당한 당사자가 아닌 사람이 삭제 요청")
    void deleteFollowRecord_whenNotGrantedPersonAccess() {
        // given
        Long followId = 1L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> followService.deleteFollowRecord(followId, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseFollow
    @Test
    @DisplayName("[정상 작동] deleteFollowRecord - 존재하지 않는 팔로우 기록")
    void deleteFollowRecord_whenNonExistedFollowRecord() {
        // given
        Long followId = 100000L;
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> followService.deleteFollowRecord(followId, member);

        // then
        assertThrows(Throwable.class, func);
    }
}
