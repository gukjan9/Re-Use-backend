package com.example.demo.follow.service;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.follow.dto.FollowMemberResponseDto;
import com.example.demo.follow.dto.FollowResponseDto;
import com.example.demo.follow.dto.FollowersResponseDto;
import com.example.demo.follow.entity.Follow;
import com.example.demo.follow.repository.FollowRepository;
import com.example.demo.member.entity.Member;
import com.example.demo.member.repository.MemberRepository;
import com.example.demo.shop.entity.Shop;
import com.example.demo.shop.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final ShopRepository shopRepository;

    @Transactional
    public ResponseEntity<FollowResponseDto> toggleShopFollow(Member memberLoggedIn, Long shopId) {
        Shop shop = findShopById(shopId);

        Optional<Follow> optionalFollow = followRepository.findByMemberAndShop_Id(memberLoggedIn, shopId);
        boolean resultOfFollowing;
        if (optionalFollow.isPresent()) {
            Follow entity = optionalFollow.get();
            resultOfFollowing = deleteFollow(entity);
        } else {
            resultOfFollowing = saveFollow(memberLoggedIn, shop);
        }

        FollowResponseDto responseDto = new FollowResponseDto(resultOfFollowing);
        return ResponseEntity.ok(responseDto);
    }

    private Shop findShopById(Long shopId) {
        return shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상점 ID는 존재하지 않습니다."));
    }

    private boolean saveFollow(Member member, Shop shop) {
        Follow entity = new Follow(member, shop);
        followRepository.save(entity);
        return true;
    }

    private boolean deleteFollow(Follow entity) {
        followRepository.delete(entity);
        return false;
    }

    public ResponseEntity<FollowResponseDto> readFollowRecordAboutTarget(Member principal, Long shopId) {
        boolean isFollowing = followRepository.existsByMemberAndShop_Id(principal, shopId);
        FollowResponseDto responseDto = new FollowResponseDto(isFollowing);
        return ResponseEntity.ok(responseDto);
    }

    public ResponseEntity<List<FollowMemberResponseDto>> readFollowersByMemberId(Long memberId) {
        List<FollowMemberResponseDto> dtoList = memberRepository.findFollowersByMember_Id(memberId).stream()
                .map(FollowMemberResponseDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    public ResponseEntity<List<FollowMemberResponseDto>> readFollowingsByMemberId(Long memberId) {
        List<FollowMemberResponseDto> dtoList = memberRepository.findFollowingsByMember_Id(memberId).stream()
                .map(FollowMemberResponseDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    public ResponseEntity<List<FollowersResponseDto>> readFollowersByShopId(Long shopId, Member memberLoggedIn) {
        List<FollowersResponseDto> dtoList = memberRepository.findFollowersByMember_Id(shopId).stream()
                .map(FollowersResponseDto::new)
                .toList();

        if(memberLoggedIn != null) {
            addFollowFlagByMemberId(dtoList, memberLoggedIn);
        }

        return ResponseEntity.ok(dtoList);
    }

    private void addFollowFlagByMemberId(List<FollowersResponseDto> dtoList, Member memberLoggedIn) {
        for(FollowersResponseDto dto : dtoList) {
            boolean isFollowing = followRepository.existsByMemberAndShop_Id(memberLoggedIn, dto.getShopId());
            dto.setPrincipalFollowing(isFollowing);
        }
    }

    public ResponseEntity<List<FollowMemberResponseDto>> readFollowingsByShopId(Long shopId) {
        List<FollowMemberResponseDto> dtoList = memberRepository.findFollowingsByMember_Id(shopId).stream()
                .map(FollowMemberResponseDto::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @Transactional
    public ResponseEntity<MessageResponseDto> deleteFollowRecord(Long followId, Member memberLoggedIn) {
        Follow follow = findFollowById(followId);
        validateTargetShopOwnerOfFollower(follow, memberLoggedIn);

        deleteFollow(follow);

        MessageResponseDto msg = new MessageResponseDto("팔로우 삭제에 성공하였습니다.", HttpStatus.OK.value());
        return ResponseEntity.status(HttpStatus.OK).body(msg);
    }

    private void validateTargetShopOwnerOfFollower(Follow follow, Member member) {
        Long followerIdInDB = follow.getShop().getMember().getId();
        Long memberIdLoggedIn = member.getId();

        if (!followerIdInDB.equals(memberIdLoggedIn)) {
            throw new AccessDeniedException("해당 팔로우 기록의 팔로워가 아닌 사람은 삭제할 수 없습니다.");
        }
    }

    private Follow findFollowById(Long followId) {
        return followRepository.findById(followId)
                .orElseThrow(() -> new IllegalArgumentException("해당 팔로우 기록은 존재하지 않습니다."));
    }
}
