package com.example.demo.chat;

import com.example.demo.chat.service.ChatRoomService;
import com.example.demo.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatRoomService chatRoomService;
    @GetMapping("/chat/room")         // 접속 주소
    public String getChatRooms() {
        return "chat/room";             // html
    }

    @GetMapping("/chat/room/{roomId}")
    public String roomDetail(@PathVariable Long roomId,
                             @AuthenticationPrincipal UserDetailsImpl userDetails,
                             Model model){
        model.addAttribute("Chatrooms", chatRoomService.getRoom(roomId, userDetails.getMember()));
        return "chat/roomdetail";
    }
}