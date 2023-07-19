package com.magnet.magnet.domain.club.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "동아리 정보")
public class ResponseClub {

    @Schema(description = "동아리 id")
    private Long id;

    @Schema(description = "동아리 이름")
    private String title;

    @Schema(description = "동아리 설명")
    private String description;

    @Schema(description = "동아리 초대 코드")
    private String invitationCode;

    @Schema(description = "동아리내 나의 역할")
    private String myRole;

    @Schema(description = "동아리내 나의 닉네임")
    private String myNickname;

    @Schema(description = "동아리 생성일")
    private LocalDateTime createdDate;

    @Schema(description = "동아리 수정일")
    private LocalDateTime modifiedDate;

}