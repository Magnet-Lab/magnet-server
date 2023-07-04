package com.magnet.magnet.domain.invitation.dao;

import com.magnet.magnet.domain.invitation.domain.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepo extends JpaRepository<Invitation, Long> {

}