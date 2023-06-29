package com.magnet.magnet.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    INVALID_INPUT_URI(HttpStatus.BAD_REQUEST, "잘못된 URI 형식입니다."),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),

    /* 404 NOT_FOUND : Resource를 찾을 수 없음 */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 유저를 찾을 수 없습니다."),

    /* 409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    SOCIAL_LOGIN_ERROR(HttpStatus.CONFLICT, "다른 경로로 가입된 유저입니다."),

    /* 500 INTERNAL_SERVER_ERROR : 서버 내부 에러 */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러가 발생했습니다."),
    SERVER_COMM_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 간 통신에 문제가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

}