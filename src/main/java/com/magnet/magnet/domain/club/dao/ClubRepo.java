package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepo extends JpaRepository<Club, Long> {

    Optional<Club> findByIdAndDeletedFalse(Long id);

    Optional<Club> findByInvitationInvitationCodeAndDeletedFalse(String invitationCode);

}