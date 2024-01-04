package com.example.demo.integration.member;

import com.amazonaws.services.s3.AmazonS3;
import com.example.demo.location.entity.Location;
import com.example.demo.location.entity.MemberLocation;
import com.example.demo.member.dto.LocationRequestDto;
import com.example.demo.member.dto.MemberInfoRequestDto;
import com.example.demo.member.dto.ShopPageMemberResponseDto;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.member.service.MemberService;
import com.example.demo.utils.LoadEnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

//@SpringBootTest
@LoadEnvironmentVariables
public class MemberModelTest {
    @Autowired
    private MemberService memberService;
    @MockBean
    private AmazonS3 amazonS3;

    @Autowired
    private MemberRepository memberRepository;

    @Retention(RetentionPolicy.RUNTIME)
    @SqlGroup({
            @Sql({
                    "classpath:data/testcase-member.sql"
            }),
            @Sql(
                    scripts = "classpath:truncate-testcases.sql",
                    executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
            )
    })
    @interface LoadTestCaseMember {}

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - nickname")
    void updateMember_whenGivenNickname() {
        // given
        String nickname = "mock nickname";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setNickname(nickname);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getNickname()).isEqualTo(nickname);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - nickname 변경 시, shopName 변경 없음.")
    void updateMember_whenGivenNickname_thenShopNameDoesNotChange() {
        // given
        String nickname = "mock nickname";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setNickname(nickname);
        Member member = memberRepository.findById(1L).orElseThrow();
        String oldShopName = member.getShop().getShopName();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getShop().getShopName()).isEqualTo(oldShopName);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - shopName")
    void updateMember_whenGivenShopName() {
        // given
        String shopName = "mock shopName";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setShopName(shopName);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getShop().getShopName()).isEqualTo(shopName);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - shopIntro")
    void updateMember_whenGivenShopIntro() {
        // given
        String shopIntro = "mock shopIntro";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setShopIntro(shopIntro);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getShop().getShopIntro()).isEqualTo(shopIntro);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - location")
    void updateMember_whenGivenLocation() {
        // given
        String location = "카카오 본사";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setLocation(location);
        Member member = memberRepository.findById(1L).orElseThrow();
        MemberLocation memberLocation = member.getLocation();

        assertThat(memberLocation.getName()).isNotEqualTo(location);
        Long latitudeBeforeUpdate = memberLocation.getLatitude();
        Long longitudeBeforeUpdate = memberLocation.getLongitude();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        MemberLocation changedLocation = changed.getLocation();

        assertThat(changedLocation.getName()).isEqualTo(location);
        assertThat(changedLocation.getLatitude())
                .isNotNull()
                .isNotEqualTo(latitudeBeforeUpdate);
        assertThat(changedLocation.getLongitude())
                .isNotNull()
                .isNotEqualTo(longitudeBeforeUpdate);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[비정상 작동] updateMember - 존재하지 않는 주소명을 기입 시, 오류 발생")
    void updateMember_whenGivenWrongLocation() {
        // given
        String location = "절대로 존재할 수가 없는 이상한 주소명";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setLocation(location);
        Member member = memberRepository.findById(1L).orElseThrow();
        MemberLocation memberLocation = member.getLocation();

        assertThat(memberLocation.getName()).isNotEqualTo(location);

        // when
        Executable func = () -> memberService.updateMember(dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMember - shopName 변경 시, nickname 변경 없음.")
    void updateMember_whenGivenShopName_thenNicknameDoesNotChange() {
        // given
        String shopName = "mock shopName";
        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setShopName(shopName);
        Member member = memberRepository.findById(1L).orElseThrow();
        String oldNickname = member.getNickname();

        // when
        memberService.updateMember(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getNickname()).isEqualTo(oldNickname);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[비정상 작동] updateMember - nickname 유일성 보장")
    void updateMember_whenNicknameMustBeUnique() {
        // given
        String nickname = "mock nickname";

        // 이미 존재하는 Nickname
        Member memberWithNickname = memberRepository.findById(2L).orElseThrow();
        memberWithNickname.setNickname(nickname);
        memberRepository.save(memberWithNickname);

        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setNickname(nickname);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> memberService.updateMember(dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[비정상 작동] updateMember - shopName 유일성 보장")
    void updateMember_whenShopNameMustBeUnique() {
        // given
        String shopName = "mock shopName";

        // 이미 존재하는 shopName
        Member memberWithNickname = memberRepository.findById(2L).orElseThrow();
        memberWithNickname.getShop().setShopName(shopName);
        memberRepository.save(memberWithNickname);

        MemberInfoRequestDto dto = new MemberInfoRequestDto();
        dto.setShopName(shopName);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> memberService.updateMember(dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateMemberLocations")
    void updateMember_whenGivenExistedLocation() {
        // given
        // 확실히 존재하는 주소명
        String location = "카카오 본사";

        LocationRequestDto dto = new LocationRequestDto();
        dto.setLocation(location);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        memberService.updateMemberLocation(dto, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        // 의도한 주소명으로 변경
        assertThat(changed.getLocation())
                .extracting(MemberLocation::getName)
                .isEqualTo(location);
        // 위도값이 존재해야 함.
        assertThat(changed.getLocation())
                .extracting(Location::getLatitude)
                .isNotNull();
        // 경도값이 존재해야 함.
        assertThat(changed.getLocation())
                .extracting(Location::getLongitude)
                .isNotNull();
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[비정상 작동] updateMemberLocations - 존재하지 않는 주소로 변경.")
    void updateMember_whenGivenNonExistedLocation() {
        // given
        // 확실히 존재하는 주소명
        String location = "절대로 존재할 수 없는 주소명";

        LocationRequestDto dto = new LocationRequestDto();
        dto.setLocation(location);
        Member member = memberRepository.findById(1L).orElseThrow();

        // when
        Executable func = () -> memberService.updateMemberLocation(dto, member);

        // then
        assertThrows(Throwable.class, func);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] updateProfileImage")
    void updateProfileImage() throws IOException {
        // given
        URL url = new URL("https://cdn.pixabay.com/photo/2023/09/20/11/40/plants-8264654_1280.jpg");
        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", url.openStream());

        Member member = memberRepository.findById(1L).orElseThrow();
        String mainImageUrl = member.getImage().toString();
        assertThat(url.toString())
                .isNotEqualTo(mainImageUrl);

        when(amazonS3.getUrl(any(), any()))
                .thenReturn(url);

        // when
        memberService.updateProfileImage(mockMultipartFile, member);

        // then
        Member changed = memberRepository.findById(1L).orElseThrow();
        assertThat(changed.getImage().toString())
                .isNotEqualTo(mainImageUrl);
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] readMemberInShopPage")
    void readMemberInShopPage() {
        // given
        Long memberId = 1L;

        // when
        ResponseEntity<ShopPageMemberResponseDto> result = memberService.readMemberInShopPage(memberId);

        // then
        assertThat(result.getBody()).isNotNull();
    }

    @LoadTestCaseMember
    @Test
    @DisplayName("[정상 작동] deleteMember")
    void deleteMember() {
        // given
        Member member = memberRepository.findById(1L).orElseThrow();
        long numOfMemberBeforeDelete = memberRepository.count();

        // when
        memberService.deleteMember(member);

        // then
        long numOfMemberAfterDelete = memberRepository.count();
        assertThat(numOfMemberAfterDelete).isEqualTo(numOfMemberBeforeDelete - 1);
    }
}
