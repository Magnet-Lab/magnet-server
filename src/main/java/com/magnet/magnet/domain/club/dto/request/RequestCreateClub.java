package com.magnet.magnet.domain.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "동아리 생성 요청")
public class RequestCreateClub {

    @Schema(description = "동아리 이름")
    private String title;

    @Schema(description = "동아리 설명")
    private String description;

}