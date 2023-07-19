package com.magnet.magnet.domain.post.category.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "카테고리 정보 응답")
public class ResponseCategory {

    @Schema(description = "카테고리 id")
    private Long categoryId;

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "카테고리 이름")
    private String categoryTitle;

    @Schema(description = "카테고리 설명")
    private String categoryDescription;

    @Schema(description = "카테고리 접근 권한")
    private String categoryPermissionRange;

    @Schema(description = "카테고리 생성일")
    private LocalDateTime createdDate;

    @Schema(description = "카테고리 수정일")
    private LocalDateTime modifiedDate;

}