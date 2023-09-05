package com.magnet.magnet.domain.post.category.app.impl;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.club.entity.ClubUser;
import com.magnet.magnet.domain.post.category.app.CategoryService;
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.entity.Category;
import com.magnet.magnet.domain.post.category.dto.request.RequestCreateCategory;
import com.magnet.magnet.domain.post.category.dto.request.RequestUpdateCategory;
import com.magnet.magnet.domain.post.category.dto.response.ResponseCategory;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.entity.User;
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
        User currentUser = getUserByClubAndEmail(findClub, email);

        validateAdminRole(findClub, currentUser);

        validateCategoryTitle(findClub, dto.getCategoryTitle());

        Category.Role permissionRange = validateDtoAccessRange(dto);

        categoryRepo.save(Category.builder()
                .club(findClub)
                .title(dto.getCategoryTitle())
                .description(dto.getCategoryDescription())
                .permissionRange(permissionRange)
                .build());
    }

    @Override
    @Transactional
    public void updateCategory(RequestUpdateCategory dto, String email) {
        Category findCategory = getCategoryByIdAndDeletedFalse(dto.getCategoryId());
        Club findClub = getClubByIdAndDeletedFalse(findCategory.getClub().getId());
        User currentUser = getUserByClubAndEmail(findClub, email);

        validateAdminRole(findCategory.getClub(), currentUser);

        validateCategoryTitle(findClub, dto.getCategoryTitle());

        Category.Role permissionRange = validateDtoAccessRange(dto);

        findCategory.updateCategoryTitle(dto.getCategoryTitle());
        findCategory.updateCategoryDescription(dto.getCategoryDescription());
        findCategory.updateCategoryAccessRange(permissionRange);
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

    private User getUserByClubAndEmail(Club club, String email) {
        return club.getClubUsers().stream().map(ClubUser::getUser).filter(user -> user.getEmail().equals(email)).findFirst()
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

    private Category.Role validateDtoAccessRange(RequestCreateCategory dto) {
        Category.Role permissionRange = Category.Role.ADMIN;
        if (dto.getCategoryPermissionRange() != null) {
            try {
                permissionRange = Category.Role.valueOf(dto.getCategoryPermissionRange().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_CATEGORY_ACCESS_RANGE);
            }
        }
        return permissionRange;
    }

    private Category.Role validateDtoAccessRange(RequestUpdateCategory dto) {
        Category.Role permissionRange = Category.Role.ADMIN;
        if (dto.getCategoryPermissionRange() != null) {
            try {
                permissionRange = Category.Role.valueOf(dto.getCategoryPermissionRange().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_CATEGORY_ACCESS_RANGE);
            }
        }
        return permissionRange;
    }

    private void validateCategoryTitle(Club club, String categoryTitle) {
        if (categoryRepo.existsByClubAndTitleAndDeletedFalse(club, categoryTitle)) {
            throw new CustomException(ErrorCode.CATEGORY_TITLE_DUPLICATED);
        }
    }

}