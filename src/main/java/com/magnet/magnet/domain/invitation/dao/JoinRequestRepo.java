package com.magnet.magnet.domain.invitation.dao;

import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.invitation.entity.JoinRequest;
import com.magnet.magnet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepo extends JpaRepository<JoinRequest, Long> {

    List<JoinRequest> findAllByClubAndStatus(Club club, JoinRequest.Status status);

    Optional<JoinRequest> findByIdAndStatus(Long id, JoinRequest.Status status);

    Optional<JoinRequest> findByClubAndUserAndStatus(Club club, User user, JoinRequest.Status status);

}