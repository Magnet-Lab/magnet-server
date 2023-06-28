package com.magnet.magnet.domain.auth.api;

import com.magnet.magnet.domain.auth.app.AuthService;
import com.magnet.magnet.domain.auth.dto.request.RequestLogin;
import com.magnet.magnet.domain.auth.dto.response.ResponseToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/login/oauth2")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(
            summary = "코드와 가입 경로로 회원가입 및 로그인",
            description = "존재하지 않은 유저일 경우 회원가입 진행 후 로그인합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "서버에 이메일이 없거나, 코드가 유효하지 않음")
            })
    public ResponseEntity<ResponseToken> login(@RequestBody RequestLogin dto) {
        return ResponseEntity.ok(authService.joinAndLogin(dto));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃",
            description = "로그아웃합니다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "엑세스 토큰이 유효하지 않음"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)")
            })
    public ResponseEntity<String> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.ok("로그아웃 완료");
    }

    @GetMapping("/code/{registrationId}")
    @Operation(
            summary = "코드 및 가입 경로 확인",
            description = "테스트용",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            })
    public String socialLogin(@RequestParam String code, @PathVariable String registrationId) {
        return authService.socialLogin(code, registrationId);
    }

}