package com.magnet.magnet.domain.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "동아리 인원 관리 요청")
public class RequestManagement {

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "유저 id")
    private Long userId;

}