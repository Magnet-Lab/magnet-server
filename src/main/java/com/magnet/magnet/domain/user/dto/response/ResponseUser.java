package com.magnet.magnet.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "유저 정보")
public class ResponseUser {

    @Schema(description = "유저 id")
    private Long id;

    @Schema(description = "유저 이메일")
    private String email;

    @Schema(description = "유저 닉네임")
    private String nickname;

    @Schema(description = "유저 가입 경로")
    private String registrationId;

    @Schema(description = "유저 uid")
    private String uid;

}