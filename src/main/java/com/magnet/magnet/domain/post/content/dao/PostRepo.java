package com.magnet.magnet.domain.post.content.dao;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.post.content.domain.Post;
import com.magnet.magnet.domain.post.category.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepo extends JpaRepository<Post, Long> {

    Optional<Post> findByIdAndDeletedFalse(Long id);

    Page<Post> findAllByClubAndDeletedFalse(Club club, Pageable pageable);

    Page<Post> findAllByClubAndCategoryAndDeletedFalse(Club club, Category category, Pageable pageable);

}