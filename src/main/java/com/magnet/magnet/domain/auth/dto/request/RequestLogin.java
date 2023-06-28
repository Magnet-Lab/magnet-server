package com.magnet.magnet.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 요청 DTO")
public class RequestLogin {

    @Schema(description = "소셜 로그인 시 얻을 수 있는 코드")
    private String code;

    @Schema(description = "로그인 시 사용할 서비스 (구글, 카카오 등)")
    private String registrationId;

}