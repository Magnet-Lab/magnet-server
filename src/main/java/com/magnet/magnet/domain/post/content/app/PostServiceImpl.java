package com.magnet.magnet.domain.post.content.app;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.post.content.dao.PostRepo;
import com.magnet.magnet.domain.post.content.domain.Post;
import com.magnet.magnet.domain.post.content.dto.request.RequestCreatePost;
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

    private final CategoryRepo categoryRepo;

    @Override
    @Transactional
    public ResponsePost createPost(RequestCreatePost dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());

        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        Post createPost = postRepo.save(Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .club(findClub)
                .writer(currentUser)
                .category(getCategoryByTitleAndClubAndDeletedFalse(dto.getCategoryTitle(), findClub))
                .location(dto.getLocation())
                .build());

        // TODO: 동아리에 가입한 유저 또는 카테고리를 구독한 유저들에게 알림 보내기

        return ResponsePost.builder()
                .id(createPost.getId())
                .title(createPost.getTitle())
                .content(createPost.getContent())
                .writer(createPost.getWriter().getNickname())
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

        validateAdminRole(findClub, currentUser);

        Post findPost = getPostByIdAndDeletedFalse(dto.getPostId());

        findPost.updateTitle(dto.getTitle());
        findPost.updateContent(dto.getContent());
        findPost.updateCategory(getCategoryByTitleAndClubAndDeletedFalse(dto.getCategoryTitle(), findClub));

        Post updatePost = postRepo.save(findPost);

        return ResponsePost.builder()
                .id(updatePost.getId())
                .title(updatePost.getTitle())
                .content(updatePost.getContent())
                .writer(updatePost.getWriter().getNickname())
                .categoryTitle(updatePost.getCategory().getTitle())
                .location(updatePost.getLocation())
                .createdDate(updatePost.getCreatedDate())
                .modifiedDate(updatePost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponsePost deletePost(Long clubId, Long postId, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);

        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        Post findPost = getPostByIdAndDeletedFalse(postId);

        findPost.deletePost();
        postRepo.save(findPost);

        return ResponsePost.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .content(findPost.getContent())
                .writer(findPost.getWriter().getNickname())
                .categoryTitle(findPost.getCategory().getTitle())
                .location(findPost.getLocation())
                .createdDate(findPost.getCreatedDate())
                .modifiedDate(findPost.getModifiedDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ResponsePost getPost(Long postId) {
        return postRepo.findByIdAndDeletedFalse(postId)
                .map(post -> ResponsePost.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .writer(post.getWriter().getNickname())
                        .categoryTitle(post.getCategory().getTitle())
                        .location(post.getLocation())
                        .createdDate(post.getCreatedDate())
                        .modifiedDate(post.getModifiedDate())
                        .build())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
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
                        .writer(post.getWriter().getNickname())
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
                        .writer(post.getWriter().getNickname())
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

    private void validateAdminRole(Club club, User user) {
        if (club.getUserRole(user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }
    }

}