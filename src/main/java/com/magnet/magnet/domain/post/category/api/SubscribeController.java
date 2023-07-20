package com.magnet.magnet.domain.post.category.api;

import com.magnet.magnet.domain.post.category.app.SubscribeService;
import com.magnet.magnet.domain.post.category.dto.response.ResponseSubscribe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "카테고리 구독")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/subscribe")
public class SubscribeController {

    private final SubscribeService subscribeService;

    @PostMapping("/{category_id}")
    @Operation(
            summary = "카테고리 구독",
            description = "카테고리를 구독합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> subscribe(@PathVariable("category_id") Long categoryId, Principal principal) {
        subscribeService.subscribe(categoryId, principal.getName());
        return ResponseEntity.ok("카테고리 구독 완료.");
    }

    @PatchMapping("/{subscribe_id}")
    @Operation(
            summary = "카테고리 구독 취소",
            description = "카테고리 구독을 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> unSubscribe(@PathVariable("subscribe_id") Long subscribeId, Principal principal) {
        subscribeService.unSubscribe(subscribeId, principal.getName());
        return ResponseEntity.ok("카테고리 구독 취소 완료.");
    }

    @GetMapping("/list")
    @Operation(
            summary = "구독한 카테고리 목록",
            description = "구독한 카테고리 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<List<ResponseSubscribe>> getSubscribeList(Principal principal) {
        return ResponseEntity.ok(subscribeService.getSubscribeList(principal.getName()));
    }

}