package com.magnet.magnet.domain.club.domain;

import com.magnet.magnet.domain.user.domain.User;
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

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        ADMIN, USER
    }

    @Builder.Default
    private boolean deleted = false;

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