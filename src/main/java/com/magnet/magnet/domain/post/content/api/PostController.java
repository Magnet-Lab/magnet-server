package com.magnet.magnet.domain.post.content.api;

import com.magnet.magnet.domain.post.content.app.PostService;
import com.magnet.magnet.domain.post.content.dto.request.RequestWritePost;
import com.magnet.magnet.domain.post.content.dto.request.RequestUpdatePost;
import com.magnet.magnet.domain.post.content.dto.response.ResponsePost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "게시글")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping("/write")
    @Operation(
            summary = "동아리 내 게시글 생성",
            description = "동아리 내 게시글을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponsePost> writePost(@RequestBody RequestWritePost dto, Principal principal) {
        return ResponseEntity.ok(postService.writePost(dto, principal.getName()));
    }

    @PutMapping("/update")
    @Operation(
            summary = "동아리 내 게시글 정보 수정",
            description = "동아리 내 게시글 정보를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponsePost> updatePost(@RequestBody RequestUpdatePost dto, Principal principal) {
        return ResponseEntity.ok(postService.updatePost(dto, principal.getName()));
    }

    @PatchMapping("/delete/{clubId}/{postId}")
    @Operation(
            summary = "동아리 내 게시글 삭제",
            description = "동아리 내 게시글을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponsePost> deletePost(@PathVariable Long clubId, @PathVariable Long postId, Principal principal) {
        return ResponseEntity.ok(postService.deletePost(clubId, postId, principal.getName()));
    }

    @GetMapping("/{postId}")
    @Operation(
            summary = "게시글 조회",
            description = "게시글을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<ResponsePost> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/list/{clubId}")
    @Operation(
            summary = "동아리 내 게시글 목록 조회",
            description = "동아리 내 게시글 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<?> getPostList(@PathVariable Long clubId, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostList(clubId, pageable));
    }

    @GetMapping("/list/{clubId}/{categoryTitle}")
    @Operation(
            summary = "동아리 내 게시글 목록 조회 (카테고리)",
            description = "동아리 내 게시글 목록을 조회합니다. (카테고리)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<?> getPostListByCategoryTitle(@PathVariable Long clubId, @PathVariable String categoryTitle, Pageable pageable) {
        return ResponseEntity.ok(postService.getPostListByCategoryTitle(clubId, categoryTitle, pageable));
    }

}