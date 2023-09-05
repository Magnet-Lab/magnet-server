package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.entity.Club;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepo extends JpaRepository<Club, Long> {

    @EntityGraph(attributePaths = {"clubUsers", "clubUsers.user"})
    Optional<Club> findByIdAndDeletedFalse(Long id);

    Optional<Club> findByInvitationInvitationCodeAndDeletedFalse(String invitationCode);

    Optional<Club> findByIdAndClubUsers_User_EmailAndDeletedFalse(Long id, String email);

}