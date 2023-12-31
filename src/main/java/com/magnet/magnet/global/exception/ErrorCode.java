package com.magnet.magnet.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_INPUT_URI(HttpStatus.BAD_REQUEST, "잘못된 URI 형식입니다."),
    CLUB_LIMIT_EXCEED(HttpStatus.BAD_REQUEST, "동아리 생성 제한을 초과했습니다."),
    ACCESS_DENIED(HttpStatus.BAD_REQUEST, "접근 권한이 없습니다."),
    INVALID_CATEGORY_ACCESS_RANGE(HttpStatus.BAD_REQUEST, "카테고리 접근 범위가 잘못되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "파라미터 값이 잘못되었습니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    POST_OWNER_MISMATCH(HttpStatus.UNAUTHORIZED, "게시글 작성자만 수정할 수 있습니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다."),
    CLUB_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 동아리를 찾을 수 없습니다."),
    CLUB_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "관리자에 해당하지 않거나 가입되지 않은 유저입니다."),
    JOIN_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 가입 요청을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 게시글을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 카테고리를 찾을 수 없습니다."),
    SUBSCRIBE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 구독을 찾을 수 없습니다."),

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    SOCIAL_LOGIN_ERROR(HttpStatus.CONFLICT, "다른 경로로 가입된 유저입니다."),
    JOIN_REQUEST_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 가입 요청이 존재합니다."),
    CLUB_USER_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 해당 동아리에 가입된 유저입니다."),
    INVITATION_CODE_ALREADY_EXIST(HttpStatus.CONFLICT, "이미 존재하는 초대 코드입니다."),
    SUBSCRIBE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 구독 중인 카테고리입니다."),
    POSTS_EXIST_IN_CATEGORY(HttpStatus.CONFLICT, "해당 카테고리에 게시글이 존재합니다."),
    CATEGORY_TITLE_DUPLICATED(HttpStatus.CONFLICT, "해당 동아리에 이미 존재하는 카테고리 타이틀입니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 내부 에러 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),
    SERVER_COMM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 간 통신에 문제가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}