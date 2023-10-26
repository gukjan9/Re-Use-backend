package com.example.demo.member.entity;

import com.example.demo.chat.entity.ChatMessage;
import com.example.demo.chat.entity.ChatRoom;
import com.example.demo.follow.entity.Follow;
import com.example.demo.location.entity.MemberLocation;
import com.example.demo.shop.entity.Shop;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Entity @Table
@Getter @Setter @NoArgsConstructor
public class Member implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "phone_num")
    private String phoneNum;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberLocation location;

    @Column(name = "image", nullable = true)
    private URL image;

    @OneToOne(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    private Shop shop;

    @OneToMany(mappedBy = "member", cascade = {CascadeType.REMOVE})
    private List<Follow> followList = new ArrayList<>();

    @OneToMany(mappedBy = "seller", cascade = {CascadeType.REMOVE})
    private List<ChatRoom> sellerChatRoomList = new ArrayList<>();

    @OneToMany(mappedBy = "consumer")
    private List<ChatRoom> consumerChatRoomList = new ArrayList<>();

//    @OneToOne(mappedBy = "sender")
//    private ChatMessage chatMessage;

    public Member(String username, String nickname) {
        this.username = username;
        this.nickname = nickname;
        this.shop = new Shop(this);
        this.location = new MemberLocation(this);
    }
}
