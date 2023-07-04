package com.magnet.magnet.domain.invitation.dao;

import com.magnet.magnet.domain.invitation.domain.JoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JoinRequestRepo extends JpaRepository<JoinRequest, Long> {

    List<JoinRequest> findAllByClubIdAndStatus(Long clubId, JoinRequest.Status status);

}