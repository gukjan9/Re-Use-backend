package com.example.demo.trade.dto;

import com.example.demo.category.entity.CategoryM;
import com.example.demo.config.ParameterNameConfig;
import com.example.demo.item.entity.Item;
import com.example.demo.member.entity.Member;
import com.example.demo.review.entity.Review;
import com.example.demo.shop.entity.Shop;
import com.example.demo.trade.type.State;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter @Setter @AllArgsConstructor
public class MySalesResponseDto {
    @Schema(description = "상품의 id", example = "1")
    @JsonProperty(ParameterNameConfig.Item.ID)
    private Long itemId;
    @Schema(description = "상품의 생성일", example = "2021-08-22T15:00:00")
    @JsonProperty(ParameterNameConfig.Item.CREATED_AT)
    private LocalDateTime createdAt;
    @Schema(description = "상품의 카테고리 id", example = "1")
    @JsonProperty(ParameterNameConfig.CategoryMiddle.ID)
    private Long categoryId;
    @Schema(description = "상품의 카테고리 이름", example = "남성의류")
    @JsonProperty(ParameterNameConfig.CategoryMiddle.NAME)
    private String categoryName;
    @Schema(description = "상품의 판매자 id", example = "1")
    @JsonProperty(ParameterNameConfig.Member.ID)
    private Long userId;
    @Schema(description = "상품의 판매자 닉네임", example = "user1")
    @JsonProperty(ParameterNameConfig.Member.NICKNAME)
    private String userNickname;
    @Schema(description = "상품의 이름", example = "아비렉스 가죽자켓")
    @JsonProperty(ParameterNameConfig.Item.NAME)
    private String itemName;
    @Schema(description = "상품의 가격", example = "10000")
    @JsonProperty(ParameterNameConfig.Item.PRICE)
    private int price;
    @Schema(description = "상품의 메인 이미지", example = "https://m.hoopbro.co.kr/web/product/big/202308/68034e9c48fe22a0aab33bb52b9b0f4c.jpg")
    @JsonProperty(ParameterNameConfig.Item.MAIN_IMAGE)
    private String imageUrl;
    @Schema(description = "상품의 상태. SELLEING, RESERVED, SOLDOUT 중 하나의 값을 가짐.", example = "SELLEING")
    @JsonProperty(ParameterNameConfig.Item.STATE)
    private String state;
    @Schema(description = "상품을 판매하는 상점 ID.", example = "1524")
    @JsonProperty(ParameterNameConfig.Shop.ID)
    private Long shopId;
    @Schema(description = "상품을 판매하는 상점 이름.", example = "적절한 상점명")
    @JsonProperty(ParameterNameConfig.Shop.NAME)
    private String shopName;

    @Schema(description = "리뷰가 작성되어 있는지 아닌지 여부", example = "true")
    @JsonProperty(ParameterNameConfig.Review.IS_REVIEW_WRITTEN)
    private Boolean isReviewWritten;

    @Schema(description = "리뷰id", example = "4812")
    @JsonProperty(ParameterNameConfig.Review.ID)
    private Long reviewId;


    public MySalesResponseDto(Item entity) {
        this.itemId = entity.getId();
        this.createdAt = entity.getCreatedAt();

        Optional<CategoryM> category = Optional.of(entity).map(Item::getCategoryMidId);
        this.categoryId = category.map(CategoryM::getId).orElse(null);
        this.categoryName = category.map(CategoryM::getName).orElse(null);

        Optional<Member> member = Optional.of(entity)
                .map(Item::getShop)
                .map(Shop::getMember);
        this.userId = member.map(Member::getId).orElse(null);
        this.userNickname = member.map(Member::getNickname).orElse(null);

        this.itemName = entity.getName();
        this.price = entity.getPrice();

        Optional<URL> imageUrl = Optional.of(entity).map(Item::getMain_image);
        this.imageUrl = imageUrl.map(URL::toString).orElse(null);

        Optional<State> state = Optional.of(entity).map(Item::getState);
        this.state = state.orElse(State.SELLING).name();

        Optional<Shop> shop = Optional.of(entity).map(Item::getShop);
        this.shopId = shop.map(Shop::getId).orElse(null);
        this.shopName = shop.map(Shop::getShopName).orElse(null);

        Optional<Review> reviewWrittenAtLast = Review.getReviewWrittenAtLast(entity.getReviewList());
        this.isReviewWritten = reviewWrittenAtLast.isPresent();
        this.reviewId = reviewWrittenAtLast.map(Review::getId).orElse(null);
    }
}
