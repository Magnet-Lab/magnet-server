package com.magnet.magnet.domain.post.content.app.impl;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.post.content.app.PostService;
import com.magnet.magnet.domain.post.content.dao.PostRepo;
import com.magnet.magnet.domain.post.content.domain.Post;
import com.magnet.magnet.domain.post.content.dto.request.RequestWritePost;
import com.magnet.magnet.domain.post.content.dto.request.RequestUpdatePost;
import com.magnet.magnet.domain.post.content.dto.response.ResponsePost;
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.domain.Category;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;

    private final ClubRepo clubRepo;

    private final UserRepo userRepo;

    private final ClubUserRepo clubUserRepo;

    private final CategoryRepo categoryRepo;

    @Override
    @Transactional
    public ResponsePost writePost(RequestWritePost dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        Category findCategory = getCategoryByTitleAndClubAndDeletedFalse(dto.getCategoryTitle(), findClub);
        User currentUser = getUserByEmail(email);
        ClubUser.Role currentUserRole = getUserRoleInClub(findClub, currentUser);

        validateAccessPermission(findCategory, currentUserRole);

        Post createPost = postRepo.save(Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .club(findClub)
                .writer(currentUser)
                .category(findCategory)
                .location(dto.getLocation())
                .build());

        String writerNickname = getNicknameByUserInClub(findClub, currentUser);

        // TODO: 동아리에 가입한 유저 또는 카테고리를 구독한 유저들에게 알림 보내기

        return ResponsePost.builder()
                .id(createPost.getId())
                .title(createPost.getTitle())
                .content(createPost.getContent())
                .writer(writerNickname)
                .categoryTitle(createPost.getCategory().getTitle())
                .location(createPost.getLocation())
                .createdDate(createPost.getCreatedDate())
                .modifiedDate(createPost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponsePost updatePost(RequestUpdatePost dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByEmail(email);

        validatePostOwnership(dto.getPostId(), currentUser);

        Post targetPost = getPostByIdAndDeletedFalse(dto.getPostId());

        targetPost.updatePostTitle(dto.getTitle());
        targetPost.updatePostContent(dto.getContent());
        targetPost.updatePostCategory(getCategoryByTitleAndClubAndDeletedFalse(dto.getCategoryTitle(), findClub));
        targetPost.updatePostLocation(dto.getLocation());

        User writer = targetPost.getWriter();
        String writerNickname = getNicknameByUserInClub(findClub, writer);

        return ResponsePost.builder()
                .id(targetPost.getId())
                .title(targetPost.getTitle())
                .content(targetPost.getContent())
                .writer(writerNickname)
                .categoryTitle(targetPost.getCategory().getTitle())
                .location(targetPost.getLocation())
                .createdDate(targetPost.getCreatedDate())
                .modifiedDate(targetPost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponsePost deletePost(Long clubId, Long postId, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        User currentUser = getUserByEmail(email);

        if (findClub.getUserRole(currentUser) != ClubUser.Role.ADMIN) {
            validatePostOwnership(postId, currentUser);
        }

        Post targetPost = getPostByIdAndDeletedFalse(postId);

        targetPost.deletePost();

        User writer = targetPost.getWriter();
        String writerNickname = getNicknameByUserInClub(findClub, writer);

        return ResponsePost.builder()
                .id(targetPost.getId())
                .title(targetPost.getTitle())
                .content(targetPost.getContent())
                .writer(writerNickname)
                .categoryTitle(targetPost.getCategory().getTitle())
                .location(targetPost.getLocation())
                .createdDate(targetPost.getCreatedDate())
                .modifiedDate(targetPost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePost getPost(Long postId) {
        Post findPost = getPostByIdAndDeletedFalse(postId);

        Club findClub = findPost.getClub();
        User writer = findPost.getWriter();
        String writerNickname = getNicknameByUserInClub(findClub, writer);

        return ResponsePost.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .writer(writerNickname)
                .categoryTitle(findPost.getCategory().getTitle())
                .location(findPost.getLocation())
                .createdDate(findPost.getCreatedDate())
                .modifiedDate(findPost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponsePost> getPostList(Long clubId, Pageable pageable) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);

        return postRepo.findAllByClubAndDeletedFalse(findClub, pageable)
                .map(post -> ResponsePost.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .writer(getNicknameByUserInClub(findClub, post.getWriter()))
                        .categoryTitle(post.getCategory().getTitle())
                        .location(post.getLocation())
                        .createdDate(post.getCreatedDate())
                        .modifiedDate(post.getModifiedDate())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResponsePost> getPostListByCategoryTitle(Long clubId, String categoryTitle, Pageable pageable) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        Category findCategory = getCategoryByTitleAndClubAndDeletedFalse(categoryTitle, findClub);

        return postRepo.findAllByClubAndCategoryAndDeletedFalse(findClub, findCategory, pageable)
                .map(post -> ResponsePost.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .writer(getNicknameByUserInClub(findClub, post.getWriter()))
                        .categoryTitle(post.getCategory().getTitle())
                        .location(post.getLocation())
                        .createdDate(post.getCreatedDate())
                        .modifiedDate(post.getModifiedDate())
                        .build());
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private String getNicknameByUserInClub(Club club, User user) {
        return clubUserRepo.findByClubAndUserAndDeletedFalse(club, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND))
                .getNickname();
    }

    private Club getClubByIdAndDeletedFalse(Long clubId) {
        return clubRepo.findByIdAndDeletedFalse(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private Category getCategoryByTitleAndClubAndDeletedFalse(String categoryTitle, Club club) {
        return categoryRepo.findByTitleAndClubAndDeletedFalse(categoryTitle, club)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    private Post getPostByIdAndDeletedFalse(Long postId) {
        return postRepo.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    private void validateAccessPermission(Category category, ClubUser.Role userRole) {
        if (category.getPermissionRange().equals(Category.Role.ADMIN) && userRole.equals(ClubUser.Role.USER)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
    }

    private ClubUser.Role getUserRoleInClub(Club club, User user) {
        return club.getUserRole(user);
    }

    private void validatePostOwnership(Long postId, User user) {
        Post post = getPostByIdAndDeletedFalse(postId);

        if (!post.getWriter().equals(user)) {
            throw new CustomException(ErrorCode.POST_OWNER_MISMATCH);
        }
    }

}