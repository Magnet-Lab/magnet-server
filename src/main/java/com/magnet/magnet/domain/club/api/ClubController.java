package com.magnet.magnet.domain.club.api;

import com.magnet.magnet.domain.club.app.ClubService;
import com.magnet.magnet.domain.club.dto.request.RequestCreateClub;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateClub;
import com.magnet.magnet.domain.club.dto.response.ResponseClub;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "동아리")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/club")
public class ClubController {

    private final ClubService clubService;

    @PostMapping("/create")
    @Operation(
            summary = "동아리 생성",
            description = "동아리를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> createClub(@RequestBody RequestCreateClub dto, Principal principal) {
        return ResponseEntity.ok("club id: " + clubService.createClub(dto, principal.getName()) + " 생성 완료");
    }

    @PutMapping("/update/{clubId}")
    @Operation(
            summary = "동아리 정보 수정",
            description = "동아리 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> updateClub(@PathVariable("clubId") Long id, @RequestBody RequestUpdateClub dto, Principal principal) {
        return ResponseEntity.ok("club id: " + clubService.updateClub(id, dto, principal.getName()) + " 수정 완료");
    }

    @PatchMapping("/delete/{clubId}")
    @Operation(
            summary = "동아리 삭제",
            description = "동아리를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> deleteClub(@PathVariable("clubId") Long id, Principal principal) {
        return ResponseEntity.ok("club id: " + clubService.deleteClub(id, principal.getName()) + " 삭제 완료");
    }

    @GetMapping("{clubId}")
    @Operation(
            summary = "동아리 조회",
            description = "동아리를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponseClub> getClub(@PathVariable("clubId") Long id, Principal principal) {
        return ResponseEntity.ok(clubService.getClub(id, principal.getName()));
    }

    @GetMapping("/myClub")
    @Operation(
            summary = "내 동아리 목록 조회",
            description = "내가 속한 동아리 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<List<ResponseClub>> getMyClub(Principal principal) {
        return ResponseEntity.ok(clubService.getMyClubList(principal.getName()));
    }

}