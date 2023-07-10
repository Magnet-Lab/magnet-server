package com.magnet.magnet.domain.club.dao;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubUserRepo extends JpaRepository<ClubUser, Long> {

    Optional<ClubUser> findByClubAndUserAndDeletedFalse(Club club, User user);

    List<ClubUser> findAllByUserAndDeletedFalse(User user);

    List<ClubUser> findAllByClubAndDeletedFalse(Club club);

    boolean existsByClubAndUserAndDeletedFalse(Club club, User user);

    int countByUserAndDeletedFalse(User user);

}