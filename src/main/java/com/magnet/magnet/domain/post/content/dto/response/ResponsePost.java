package com.magnet.magnet.domain.post.content.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "게시글 정보")
public class ResponsePost {

    @Schema(description = "게시글 id")
    private Long id;

    @Schema(description = "게시글 제목")
    private String title;

    @Schema(description = "게시글 내용")
    private String content;

    @Schema(description = "게시글 작성자")
    private String writer;

    @Schema(description = "게시글 카테고리 이름")
    private String categoryTitle;

    @Schema(description = "게시글 위치")
    private String location;

    @Schema(description = "게시글 생성일")
    private LocalDateTime createdDate;

    @Schema(description = "게시글 수정일")
    private LocalDateTime modifiedDate;

}