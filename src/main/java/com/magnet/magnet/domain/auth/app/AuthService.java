package com.magnet.magnet.domain.auth.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.magnet.magnet.domain.user.dao.UserRepository;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.domain.auth.dto.request.RequestLogin;
import com.magnet.magnet.domain.auth.dto.response.ResponseToken;
import com.magnet.magnet.global.auth.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Environment env;
    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;

    // 회원가입 및 로그인
    @Transactional
    public ResponseToken joinAndLogin(RequestLogin dto) {
        JsonNode userResourceNode = getUserResource(getAccessToken(dto.getCode(), dto.getRegistrationId()), dto.getRegistrationId());

        String email = userResourceNode.get("email").asText(); // 이메일
        String nickname = userResourceNode.get("name").asText(); // 닉네임
        String registrationId = dto.getRegistrationId(); // 가입 경로
        String uid = userResourceNode.get("id").asText(); // 고유 식별자

        // 로그인을 한 유저가 처음 왔다면 회원가입
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .registrationId(registrationId)
                    .uid(uid)
                    .build();
            userRepository.save(user);
        }

        // 토큰 발급
        return tokenProvider.createToken(email);
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

    // 코드와 가입 경로를 이용한 로그인 서비스의 엑세스 토큰 발급
    private String getAccessToken(String authorizationCode, String registrationId) {
        String clientId = env.getProperty("oauth2." + registrationId + ".client-id");
        String clientSecret = env.getProperty("oauth2." + registrationId + ".client-secret");
        String redirectUri = env.getProperty("oauth2." + registrationId + ".redirect-uri");
        String tokenUri = env.getProperty("oauth2." + registrationId + ".token-uri");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", authorizationCode);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(tokenUri, HttpMethod.POST, entity, JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    // 로그인 서비스의(구글 등) 엑세스 토큰을 이용한 유저 정보 가져오기
    private JsonNode getUserResource(String accessToken, String registrationId) {
        String resourceUri = env.getProperty("oauth2."+registrationId+".resource-uri");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity entity = new HttpEntity(headers);
        assert resourceUri != null;
        return restTemplate.exchange(resourceUri, HttpMethod.GET, entity, JsonNode.class).getBody();
    }

}