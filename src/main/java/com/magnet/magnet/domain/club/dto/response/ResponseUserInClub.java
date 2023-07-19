package com.magnet.magnet.domain.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "동아리 내 인원 정보")
public class ResponseUserInClub {

    @Schema(description = "유저 id")
    private Long userId;

    @Schema(description = "동아리 내 설정한 닉네임")
    private String clubNickname;

    @Schema(description = "기본 닉네임")
    private String defaultNickname;

    @Schema(description = "동아리 내 역할")
    private String role;

}