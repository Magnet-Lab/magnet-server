package com.magnet.magnet.global.config;

import com.magnet.magnet.global.auth.jwt.JwtFilter;
import com.magnet.magnet.global.auth.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${cors.allowed-origins}")
    String[] corsOrigins; // 허용할 Origin(요청 주소) 목록
    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable) // 기본 설정 비활성화
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 정책 비활성화
                )
                .csrf(AbstractHttpConfigurer::disable) // csrf 비활성화
                .logout(AbstractHttpConfigurer::disable) // logout 비활성화
                .authorizeHttpRequests((authorizeHttpRequests) ->
                        authorizeHttpRequests
                                .requestMatchers("/", "/swagger-ui/**", "/api-docs/**", "/login/oauth2/**").permitAll() // 누구나 접근 가능한 경로
                                .anyRequest().authenticated() // 나머지 경로는 인증 필요
                )
                .cors((cors) -> cors.configurationSource(configurationSource())) // cors 설정
                .addFilterBefore(new JwtFilter(tokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class) // JwtFilter 등록
                .build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of(corsOrigins));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Access-Control-Allow-Credentials", "Authorization", "Set-Cookie"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}