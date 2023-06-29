package com.magnet.magnet.domain.user.domain;

import com.magnet.magnet.global.common.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일 필요")
    @Column(unique = true)
    private String email;

    private String nickname;

    @NotBlank(message = "가입 경로 필요")
    private String registrationId;

    private String uid;

}