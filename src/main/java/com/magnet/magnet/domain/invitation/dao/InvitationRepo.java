package com.magnet.magnet.domain.invitation.dao;

import com.magnet.magnet.domain.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvitationRepo extends JpaRepository<Invitation, Long> {

    Optional<Invitation> findByInvitationCode(String invitationCode);

}