package com.magnet.magnet.domain.post.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "게시글 생성 요청")
public class RequestCreatePost {

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 카테고리 이름")
    private String categoryTitle;

    @Schema(description = "게시글 위치")
    private String location;

}