package com.magnet.magnet.domain.invitation.dao;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.invitation.domain.JoinRequest;
import com.magnet.magnet.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JoinRequestRepo extends JpaRepository<JoinRequest, Long> {

    List<JoinRequest> findAllByClubAndStatus(Club club, JoinRequest.Status status);

    Optional<JoinRequest> findByIdAndStatus(Long id, JoinRequest.Status status);

    boolean existsByClubAndUserAndStatus(Club club, User user, JoinRequest.Status status);

}