package com.magnet.magnet.domain.invitation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "동아리 가입 요청")
public class ResponseJoinRequest {

    @Schema(description = "동아리 가입 요청 id")
    private Long id;

    @Schema(description = "동아리 id")
    private Long clubId;

    @Schema(description = "동아리 이름")
    private String clubTitle;

    @Schema(description = "동아리 가입 요청한 유저 id")
    private Long userId;

    @Schema(description = "동아리 가입 요청한 유저 이름")
    private String userNickname;

    @Schema(description = "동아리 가입 요청 생성일")
    private LocalDateTime createdDate;

    @Schema(description = "동아리 가입 요청 수정일")
    private LocalDateTime modifiedDate;

}