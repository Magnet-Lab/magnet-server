package com.magnet.magnet.domain.post.category.dao;

import com.magnet.magnet.domain.post.category.entity.Category;
import com.magnet.magnet.domain.post.category.entity.Subscribe;
import com.magnet.magnet.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepo extends JpaRepository<Subscribe, Long> {

    Optional<Subscribe> findByIdAndStatus(Long id, Subscribe.Status status);

    List<Subscribe> findAllByUserAndStatus(User user, Subscribe.Status status);

    boolean existsByUserAndCategoryAndStatus(User user, Category category, Subscribe.Status status);

}