package com.magnet.magnet.domain.club.api;

import com.magnet.magnet.domain.club.app.ClubManageService;
import com.magnet.magnet.domain.club.dto.request.RequestManagement;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "동아리 인원 관리")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/club")
public class ClubManageController {

    private final ClubManageService clubManageService;

    @PutMapping("/admin")
    @Operation(
            summary = "동아리원에게 ADMIN 권한 부여",
            description = "동아리원에게 ADMIN 권한을 부여합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> setAdmin(@RequestBody RequestManagement dto, Principal principal) {
        clubManageService.setUserAsAdmin(dto, principal.getName());
        return ResponseEntity.ok("ADMIN 변경 완료");
    }

    @PutMapping("/user")
    @Operation(
            summary = "동아리원에게 USER 권한 부여",
            description = "동아리원에게 USER 권한을 부여합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> setUser(@RequestBody RequestManagement dto, Principal principal) {
        clubManageService.setUserAsUser(dto, principal.getName());
        return ResponseEntity.ok("USER 변경 완료");
    }

    @PatchMapping("/delete")
    @Operation(
            summary = "동아리원 삭제",
            description = "동아리원을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> deleteUser(@RequestBody RequestManagement dto, Principal principal) {
        clubManageService.deleteUser(dto, principal.getName());
        return ResponseEntity.ok("삭제 완료");
    }

}