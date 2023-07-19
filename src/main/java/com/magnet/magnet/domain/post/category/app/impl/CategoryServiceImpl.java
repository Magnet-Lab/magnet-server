package com.magnet.magnet.domain.post.category.app.impl;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.post.category.app.CategoryService;
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.domain.Category;
import com.magnet.magnet.domain.post.category.dto.request.RequestCreateCategory;
import com.magnet.magnet.domain.post.category.dto.request.RequestUpdateCategory;
import com.magnet.magnet.domain.post.category.dto.response.ResponseCategory;
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
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    private final UserRepo userRepo;

    private final ClubRepo clubRepo;

    @Override
    @Transactional
    public void createCategory(RequestCreateCategory dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        categoryRepo.save(Category.builder()
                .club(findClub)
                .title(dto.getCategoryTitle())
                .description(dto.getCategoryDescription())
                .permissionRange(Category.Role.valueOf(dto.getCategoryPermissionRange().toUpperCase()))
                .build());
    }

    @Override
    @Transactional
    public void updateCategory(RequestUpdateCategory dto, String email) {
        Category findCategory = getCategoryByIdAndDeletedFalse(dto.getCategoryId());
        User currentUser = getUserByEmail(email);

        validateAdminRole(findCategory.getClub(), currentUser);

        findCategory.updateCategoryTitle(dto.getCategoryTitle());
        findCategory.updateCategoryDescription(dto.getCategoryDescription());

        validateDtoAccessRange(dto);

        findCategory.updateCategoryAccessRange(Category.Role.valueOf(dto.getCategoryPermissionRange().toUpperCase()));
    }


    @Override
    @Transactional
    public void deleteCategory(Long categoryId, String email) {
        Category findCategory = getCategoryByIdAndDeletedFalse(categoryId);
        User currentUser = getUserByEmail(email);

        throwErrorIfPostsExistInCategory(findCategory);

        validateAdminRole(findCategory.getClub(), currentUser);

        findCategory.deleteCategory();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseCategory> getCategoryList(Long clubId) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);

        List<Category> categoryList = categoryRepo.findAllByClubAndDeletedFalse(findClub);
        return categoryList.stream()
                .map(category -> ResponseCategory.builder()
                        .categoryId(category.getId())
                        .clubId(category.getClub().getId())
                        .categoryTitle(category.getTitle())
                        .categoryDescription(category.getDescription())
                        .categoryPermissionRange(category.getPermissionRange().toString())
                        .createdDate(category.getCreatedDate())
                        .modifiedDate(category.getModifiedDate())
                        .build())
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Club getClubByIdAndDeletedFalse(Long clubId) {
        return clubRepo.findByIdAndDeletedFalse(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private Category getCategoryByIdAndDeletedFalse(Long categoryId) {
        return categoryRepo.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private void throwErrorIfPostsExistInCategory(Category category) {
        if (category.getPostList().size() > 0) {
            throw new CustomException(ErrorCode.POSTS_EXIST_IN_CATEGORY);
        }
    }

    private void validateAdminRole(Club club, User user) {
        if (club.getUserRole(user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }
    }

    private void validateDtoAccessRange(RequestUpdateCategory dto) {
        if (dto.getCategoryPermissionRange() != null) {
            try {
                Category.Role.valueOf(dto.getCategoryPermissionRange().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_CATEGORY_ACCESS_RANGE);
            }
        }
    }

}