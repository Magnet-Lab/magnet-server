package com.magnet.magnet.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "모바일 로그인 요청 DTO")
public class RequestMobileLogin {

    @Schema(description = "유저 이메일")
    private String email;

    @Schema(description = "유저 기본 닉네임")
    private String defaultNickname;

    @Schema(description = "소셜 로그인 시 얻을 수 있는 코드")
    private String registrationId;

    @Schema(description = "유저 uid")
    private String uid;

}