package com.magnet.magnet.domain.post.category.api;

import com.magnet.magnet.domain.post.category.app.CategoryService;
import com.magnet.magnet.domain.post.category.dto.request.RequestCreateCategory;
import com.magnet.magnet.domain.post.category.dto.request.RequestUpdateCategory;
import com.magnet.magnet.domain.post.category.dto.response.ResponseCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@Tag(name = "게시글 카테고리")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    @Operation(
            summary = "동아리 내 카테고리 생성",
            description = "동아리 내 카테고리를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> create(@RequestBody RequestCreateCategory dto, Principal principal) {
        categoryService.createCategory(dto, principal.getName());
        return ResponseEntity.ok("카테고리 생성 완료.");
    }

    @PutMapping("/update")
    @Operation(
            summary = "동아리 내 카테고리 수정",
            description = "동아리 내 카테고리를 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> update(@RequestBody RequestUpdateCategory dto, Principal principal) {
        categoryService.updateCategory(dto, principal.getName());
        return ResponseEntity.ok("카테고리 수정 완료.");
    }

    @PatchMapping("/delete/{category_id}")
    @Operation(
            summary = "동아리 내 카테고리 삭제",
            description = "동아리 내 카테고리를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<String> delete(@PathVariable("category_id") Long categoryId, Principal principal) {
        categoryService.deleteCategory(categoryId, principal.getName());
        return ResponseEntity.ok("카테고리 삭제 완료.");
    }

    @GetMapping("/list/{club_id}")
    @Operation(
            summary = "동아리 내 카테고리 목록 조회",
            description = "동아리 내 카테고리 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "요청 성공"),
                    @ApiResponse(responseCode = "403", description = "인증 오류 (토큰)"),
                    @ApiResponse(responseCode = "500", description = "관리자 문의")
            })
    public ResponseEntity<List<ResponseCategory>> list(@PathVariable("club_id") Long clubId) {
        return ResponseEntity.ok(categoryService.getCategoryList(clubId));
    }

}