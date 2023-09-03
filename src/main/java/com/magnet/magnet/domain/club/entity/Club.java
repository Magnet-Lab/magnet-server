package com.magnet.magnet.domain.club.entity;

import com.magnet.magnet.domain.invitation.entity.Invitation;
import com.magnet.magnet.domain.user.entity.User;
import com.magnet.magnet.global.common.BaseTime;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invitation_id")
    private Invitation invitation;

    @OneToMany(mappedBy = "club", fetch = FetchType.LAZY)
    private List<ClubUser> clubUsers;

    @Builder.Default
    private boolean deleted = false;

    public void updateClubTitle(String newTitle) {
        if (newTitle == null || newTitle.isEmpty() || newTitle.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.title = newTitle;
    }

    public void updateClubDescription(String newDescription) {
        if (newDescription == null || newDescription.isEmpty() || newDescription.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.description = newDescription;
    }

    public void deleteClub() {
        this.deleted = true;
    }

    public ClubUser.Role getUserRole(User user) {
        return clubUsers.stream()
                .filter(clubUser -> clubUser.getUser().equals(user) && !clubUser.isDeleted())
                .findFirst()
                .map(ClubUser::getRole)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));
    }

}