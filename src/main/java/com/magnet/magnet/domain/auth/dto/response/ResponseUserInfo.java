package com.magnet.magnet.domain.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "소셜 로그인 응답")
public class ResponseUserInfo {

    @Schema(description = "유저 이메일")
    private String email;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "유저 uid")
    private String uid;

}