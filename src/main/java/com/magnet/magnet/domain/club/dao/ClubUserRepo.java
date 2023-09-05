package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.club.entity.ClubUser;
import com.magnet.magnet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubUserRepo extends JpaRepository<ClubUser, Long> {

    Optional<ClubUser> findByClubAndUserAndDeletedFalse(Club club, User user);

    List<ClubUser> findAllByUserAndDeletedFalse(User user);

    List<ClubUser> findAllByClubAndDeletedFalse(Club club);

    int countByUserAndDeletedFalse(User user);

}