package com.magnet.magnet.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "서비스 토큰")
public class ResponseToken {

    @Schema(description = "서비스 엑세스 토큰")
    private String accessToken;

}