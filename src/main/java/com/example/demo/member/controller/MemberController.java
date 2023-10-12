package com.example.demo.member.controller;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.member.dto.LoginRequestDto;
import com.example.demo.member.dto.LoginResponseDto;
import com.example.demo.member.dto.MemberInfoRequestDto;
import com.example.demo.member.dto.SignupRequestDto;
import com.example.demo.kakao.service.KakaoService;
import com.example.demo.member.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController implements MemberDocs{
    private final MemberService memberService;
    private final KakaoService kakaoService;

    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        return memberService.signup(request);
    }

    @PutMapping("/members/me")
    public ResponseEntity<MessageResponseDto> updateMember(@Valid @RequestBody MemberInfoRequestDto request, @RequestHeader("Authorization") String token) {
        return memberService.updateMember(request, token);//token을 받아서 수정
    }

    @DeleteMapping("/members/me")
    public ResponseEntity<MessageResponseDto> deleteMember(@Valid @RequestBody LoginRequestDto request, @RequestHeader("Authorization") String token) {
        return memberService.deleteMember(request, token);//token을 받아서 삭제
    }

    // 카카오 소셜 로그인 테스트를 위한 URL
    // https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=851d6c649ed19d32afa2743c91134e57&redirect_uri=http://localhost:8080/api/auth/kakao/callback
    @GetMapping("/kakao/callback")
    public ResponseEntity<LoginResponseDto> kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        return kakaoService.kakaoLogin(code);
    }
}
