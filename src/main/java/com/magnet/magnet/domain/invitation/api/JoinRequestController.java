package com.magnet.magnet.domain.invitation.api;

import com.magnet.magnet.domain.invitation.app.JoinRequestService;
import com.magnet.magnet.domain.invitation.dto.response.ResponseJoinRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "동아리 가입")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/join")
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    @PostMapping("/request/{invitation_code}")
    @Operation(
            summary = "동아리 가입 요청 생성",
            description = "초대 코드를 사용해 동아리 가입 요청을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> createClub(@PathVariable("invitation_code") String invitationCode, Principal principal) {
        joinRequestService.createJoinRequest(invitationCode, principal.getName());
        return ResponseEntity.ok("동아리 가입 요청 생성 완료");
    }

    @GetMapping("list/{club_id}")
    @Operation(
            summary = "동아리 가입 요청 목록 조회",
            description = "해당 동아리의 관리자가 가입 요청을 조회합니다. (관리자만 가능)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<List<ResponseJoinRequest>> createClub(@PathVariable("club_id") Long clubId, Principal principal) {
        return ResponseEntity.ok(joinRequestService.getJoinRequestList(clubId, principal.getName()));
    }

    @PostMapping("/accept/{join_request_id}")
    @Operation(
            summary = "동아리 가입 요청 수락",
            description = "해당 동아리의 관리자가 가입 요청을 수락합니다. (관리자만 가능)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> acceptJoinRequest(@PathVariable("join_request_id") Long joinRequestId, Principal principal) {
        joinRequestService.acceptJoinRequest(joinRequestId, principal.getName());
        return ResponseEntity.ok("동아리 가입 요청 수락 완료");
    }

    @PostMapping("/reject/{join_request_id}")
    @Operation(
            summary = "동아리 가입 요청 거절",
            description = "해당 동아리의 관리자가 가입 요청을 거절합니다. (관리자만 가능)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> rejectJoinRequest(@PathVariable("join_request_id") Long joinRequestId, Principal principal) {
        joinRequestService.rejectJoinRequest(joinRequestId, principal.getName());
        return ResponseEntity.ok("동아리 가입 요청 거절 완료");
    }

}