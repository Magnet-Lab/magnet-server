package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubUserRepo extends JpaRepository<ClubUser, Long> {

    Optional<ClubUser> findByClubAndUserAndDeleted(Club club, User user, boolean deleted);

    List<ClubUser> findByUserAndDeleted(User user, boolean deleted);

    List<ClubUser> findAllByClubAndDeleted(Club club, boolean deleted);

}