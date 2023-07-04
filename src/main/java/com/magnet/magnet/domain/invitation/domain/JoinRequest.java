package com.magnet.magnet.domain.invitation.domain;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.common.BaseTime;
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
public class JoinRequest extends BaseTime {

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
    @Builder.Default
    private Status status = Status.WAITING;

    public enum Status {
        WAITING, ACCEPTED, REJECTED
    }

    public void acceptRequest() {
        this.status = Status.ACCEPTED;
    }

    public void rejectRequest() {
        this.status = Status.REJECTED;
    }

}