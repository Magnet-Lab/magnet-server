package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.domain.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepo extends JpaRepository<Club, Long> {

    Optional<Club> findByIdAndDeleted(Long id, boolean deleted);

    Optional<Club> findByInvitationInvitationCode(String invitationCode);

}