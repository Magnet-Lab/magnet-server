package com.magnet.magnet.domain.club.domain;

import com.magnet.magnet.domain.invitation.domain.Invitation;
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
public class Club extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @OneToOne
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @Builder.Default
    private boolean deleted = false;

    public void updateClub(String newTitle, String newDescription) {
        this.title = newTitle;
        this.description = newDescription;
    }

    public void deleteClub() {
        this.deleted = true;
    }

}