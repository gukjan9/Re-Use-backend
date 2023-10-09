package com.example.demo.item.controller;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.item.dto.ItemSearchResponseDto;
import com.example.demo.item.service.ItemService;
import com.example.demo.member.entity.Member;
import com.example.demo.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

//    @Secured("ROLE_USER")
    @PostMapping
    public ResponseEntity<MessageResponseDto> createItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestParam("image") MultipartFile image,
            @Valid @RequestParam("name") String name,
            @Valid @RequestParam("price") int price,
            @Valid @RequestParam("comment") String comment
            ) throws IOException {
        Member member = userDetails.getMember();
        return itemService.createItem(member, image, name, price, comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDto> updateItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestParam("image") MultipartFile newFile,
            @Valid @RequestParam("name") String name,
            @Valid @RequestParam("price") int price,
            @Valid @RequestParam("comment") String comment
            ) throws IOException {
        Member member = userDetails.getMember();
        return itemService.updateItem(member, id, newFile, name, price, comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseDto> deleteItem(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        Member member = userDetails.getMember();
        return itemService.deleteItem(member, id);
    }

    @GetMapping
    public ResponseEntity<List<ItemSearchResponseDto>> searchItem(
            @RequestParam(required = false) String keyword
    ) {
        return itemService.searchItem(keyword);
    }

}
