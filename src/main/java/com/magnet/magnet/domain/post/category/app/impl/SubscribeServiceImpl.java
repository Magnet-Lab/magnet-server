package com.magnet.magnet.domain.post.category.app.impl;

import com.magnet.magnet.domain.post.category.app.SubscribeService;
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.dao.SubscribeRepo;
import com.magnet.magnet.domain.post.category.domain.Category;
import com.magnet.magnet.domain.post.category.domain.Subscribe;
import com.magnet.magnet.domain.post.category.dto.response.ResponseSubscribe;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeRepo subscribeRepo;

    private final CategoryRepo categoryRepo;

    private final UserRepo userRepo;

    @Override
    @Transactional
    public void subscribe(Long categoryId, String email) {
        User findUser = getUserByEmail(email);

        Category findCategory = getCategoryByIdAndDeletedFalse(categoryId);

        // 해당 카테고리에 이미 구독 중인지 확인
        validateActiveSubscription(findUser, findCategory);

        subscribeRepo.save(Subscribe.builder()
                .user(findUser)
                .category(findCategory)
                .build());
    }

    @Override
    @Transactional
    public void unSubscribe(Long categoryId, String email) {
        Subscribe findSubscribe = getSubscribeByIdAndStatus(categoryId, Subscribe.Status.SUBSCRIBE);

        findSubscribe.unSubscribe();
        subscribeRepo.save(findSubscribe);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseSubscribe> getSubscribeList(String email) {
        return subscribeRepo.findAllByUserAndStatus(getUserByEmail(email), Subscribe.Status.SUBSCRIBE).stream()
                .map(subscribe -> ResponseSubscribe.builder()
                        .subscribeId(subscribe.getId())
                        .categoryId(subscribe.getCategory().getId())
                        .categoryTitle(subscribe.getCategory().getTitle())
                        .categoryDescription(subscribe.getCategory().getDescription())
                        .clubId(subscribe.getCategory().getClub().getId())
                        .clubTitle(subscribe.getCategory().getClub().getTitle())
                        .createdDate(subscribe.getCreatedDate())
                        .modifiedDate(subscribe.getModifiedDate())
                        .build())
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Category getCategoryByIdAndDeletedFalse(Long categoryId) {
        return categoryRepo.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Subscribe getSubscribeByIdAndStatus(Long subscribeId, Subscribe.Status status) {
        return subscribeRepo.findByIdAndStatus(subscribeId, status)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIBE_NOT_FOUND));
    }

    private void validateActiveSubscription(User user, Category category) {
        if (subscribeRepo.existsByUserAndCategoryAndStatus(user, category, Subscribe.Status.SUBSCRIBE)) {
            throw new CustomException(ErrorCode.SUBSCRIBE_ALREADY_EXISTS);
        }
    }

}