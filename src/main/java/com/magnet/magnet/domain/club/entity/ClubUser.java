package com.magnet.magnet.domain.club.entity;

import com.magnet.magnet.domain.user.entity.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ClubUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    @Builder.Default
    private boolean deleted = false;

    public void updateNickname(String newNickname) {
        if (newNickname == null || newNickname.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.nickname = newNickname;
    }

    public void updateRoleToAdmin() {
        this.role = Role.ADMIN;
    }

    public void updateRoleToUser() {
        this.role = Role.USER;
    }

    public void deleteClubUser() {
        this.deleted = true;
    }

}