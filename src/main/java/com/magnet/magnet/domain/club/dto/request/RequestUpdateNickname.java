package com.magnet.magnet.domain.club.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "동아리에서 사용하는 닉네임 수정 요청")
public class RequestUpdateNickname {

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "닉네임")
    private String nickname;

}