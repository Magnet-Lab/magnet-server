package com.magnet.magnet.domain.post.content.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "게시글 정보 수정 요청")
public class RequestUpdatePost {

    @Schema(description = "게시글 id")
    private Long postId;

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