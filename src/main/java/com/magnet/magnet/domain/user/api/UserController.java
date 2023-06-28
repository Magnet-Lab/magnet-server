package com.magnet.magnet.domain.user.api;

import com.magnet.magnet.domain.user.app.UserService;
import com.magnet.magnet.domain.user.dto.response.ResponseUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "유저")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/myInfo")
    @Operation(
            summary = "내 정보 확인",
            description = "로그인된 유저의 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponseUser> myInfo(Principal principal) {
        return ResponseEntity.ok(userService.myInfo(principal));
    }

}