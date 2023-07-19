package com.magnet.magnet.domain.auth.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.magnet.magnet.domain.auth.dto.response.ResponseUserInfo;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.domain.auth.dto.request.RequestLogin;
import com.magnet.magnet.domain.auth.dto.response.ResponseToken;
import com.magnet.magnet.global.auth.jwt.TokenProvider;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepo userRepo;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    // 코드와 가입경로를 통해 회원가입 및 로그인
    @Transactional
    public ResponseToken registerAndLogin(RequestLogin dto) {
        // Resource 서버로부터 코드를 통해 유저 정보를 가져오기
        ResponseUserInfo userInfo = getUserInfo(getUserResource(getAccessToken(dto.getCode(), dto.getRegistrationId()), dto.getRegistrationId()));

        // 유저가 존재한다면 토큰 발급, 존재하지 않는다면 회원가입 후 토큰 발급
        userRepo.findByEmail(userInfo.getEmail())
                .ifPresentOrElse(
                        user -> {
                            // 가입 경로가 다르다면 에러
                            if (!user.getRegistrationId().equals(dto.getRegistrationId())) {
                                throw new CustomException(ErrorCode.SOCIAL_LOGIN_ERROR);
                            }
                        },
                        () -> {
                            // 처음 로그인하는 유저라면 회원가입
                            userRepo.save(
                                    User.builder()
                                    .email(userInfo.getEmail())
                                    .defaultNickname(userInfo.getNickname())
                                    .registrationId(dto.getRegistrationId())
                                    .uid(userInfo.getUid())
                                    .build()
                            );
                        }
                );

        // 토큰 발급
        return tokenProvider.createToken(userInfo.getEmail());
    }

    // 로그아웃
    public void logout(HttpServletRequest request) {
        // accessToken 값
        String accessToken = tokenProvider.resolveToken(request);

        // 만료 기간
        Long expiration = tokenProvider.getExpiration(accessToken);

        // 블랙 리스트 추가
        redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
    }

    // 코드와 가입 경로 확인
    public String socialLogin(String code, String registrationId) {
        return code + " " + registrationId;
    }

    // Resource 서버로부터 유저 정보를 가져오기
    private ResponseUserInfo getUserInfo(JsonNode userResource) {
        return ResponseUserInfo.builder()
                .email(userResource.get("email").asText()) // 이메일
                .nickname(userResource.get("name").asText()) // 닉네임
                .uid(userResource.get("id").asText()) // 고유 식별자
                .build();
    }

    // 로그인 서비스의(구글 등) 엑세스 토큰을 이용한 유저 정보 가져오기
    private JsonNode getUserResource(String accessToken, String registrationId) {
        // Resource 서버 URI (구글, 카카오 등)
        String resourceUri = env.getProperty("oauth2." + registrationId + ".resource-uri");

        // 헤더에 엑세스 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        // 헤더 추가
        HttpEntity entity = new HttpEntity(headers);

        if (ObjectUtils.isEmpty(resourceUri)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_URI);
        }

        // 유저 정보 가져오기
        try {
            return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_COMM_ERROR);
        }
    }

    // 코드와 가입 경로를 이용한 로그인 서비스의 엑세스 토큰 발급
    private String getAccessToken(String authorizationCode, String registrationId) {
        // 프로퍼티 값 가져오기
        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
        String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

        // 파라미터 추가
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 헤더와 파라미터 추가
        HttpEntity entity = new HttpEntity(params, headers);

        if (ObjectUtils.isEmpty(tokenUri)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_URI);
        }

        // 엑세스 토큰 가져오기
        try {
            return restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class).getBody().get("access_token").asText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SERVER_COMM_ERROR);
        }
    }

}