package com.magnet.magnet.domain.post.category.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "카테고리 생성 요청")
public class RequestCreateCategory {

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "카테고리 이름")
    private String categoryTitle;

    @Schema(description = "카테고리 설명")
    private String categoryDescription;
    
    @Schema(description = "카테고리 접근 권한")
    private String categoryPermissionRange;

}