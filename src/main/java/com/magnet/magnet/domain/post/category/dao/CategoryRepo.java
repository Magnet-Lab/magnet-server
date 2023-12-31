package com.magnet.magnet.domain.post.category.dao;

import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.post.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndDeletedFalse(Long id);

    Optional<Category> findByTitleAndClubAndDeletedFalse(String categoryTitle, Club club);

    List<Category> findAllByClubAndDeletedFalse(Club club);

    boolean existsByClubAndTitleAndDeletedFalse(Club club, String categoryTitle);

}