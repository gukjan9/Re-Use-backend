package com.example.demo.review.dto;


import com.example.demo.config.ParameterNameConfig;
import com.example.demo.item.entity.Item;
import com.example.demo.member.entity.Member;
import com.example.demo.review.entity.Review;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;


@Getter
@Setter
public class ReviewResponseDto {
    @Schema(description = "리뷰 id", example = "1")
    @JsonProperty(ParameterNameConfig.Review.ID)
    private Long id;
    @Schema(description = "리뷰 내용", example = "맛있어요!")
    @JsonProperty(ParameterNameConfig.Review.COMMENT)
    private String comment;
    @Schema(description = "리뷰 작성 시간", example = "2021-08-09T15:00:00")
    @JsonProperty(ParameterNameConfig.Review.CREATED_AT)
    private LocalDateTime createdAt;

    @Schema(description = "리뷰한 상품 ID", example = "1423")
    @JsonProperty(ParameterNameConfig.Item.ID)
    private Long itemId;
    @Schema(description = "리뷰한 상품 이름", example = "아우터")
    @JsonProperty(ParameterNameConfig.Item.NAME)
    private String itemName;
    @Schema(description = "리뷰한 상품 이미지", example = "https://cdn.pixabay.com/photo/2023/10/14/09/20/mountains-8314422_1280.png")
    @JsonProperty(ParameterNameConfig.Item.MAIN_IMAGE)
    private String itemImageUrl;

    @Schema(description = "리뷰어 ID", example = "1423")
    @JsonProperty(ParameterNameConfig.Member.ID)
    private Long reviewerId;
    @Schema(description = "리뷰어 이름", example = "iksadnorth")
    @JsonProperty(ParameterNameConfig.Member.NICKNAME)
    private String reviewerName;

    @Schema(description = "리뷰 점수", example = "3.5463")
    @JsonProperty(ParameterNameConfig.Review.RATING)
    private Double reviewRating;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.comment = review.getComment();
        this.createdAt = review.getCreatedAt();

        Optional<Item> item = Optional.of(review).map(Review::getItem);
        this.itemId = item.map(Item::getId).orElse(null);
        this.itemName = item.map(Item::getName).orElse(null);
        this.itemImageUrl = item.map(Item::getMain_image)
                .map(Object::toString)
                .orElse(null);

        Optional<Member> member = Optional.of(review).map(Review::getMember);
        this.reviewerId = member.map(Member::getId).orElse(null);
        this.reviewerName = member.map(Member::getNickname).orElse(null);

        this.reviewRating = review.getRating();
    }

}
