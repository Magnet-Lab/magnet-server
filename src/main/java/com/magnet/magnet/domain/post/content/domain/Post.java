package com.magnet.magnet.domain.post.content.domain;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.post.category.domain.Category;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.common.BaseTime;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String location;

    @Builder.Default
    private boolean deleted = false;

    public void updatePostTitle(String newTitle) {
        if (newTitle == null || newTitle.isEmpty() || newTitle.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.title = newTitle;
    }

    public void updatePostContent(String newContent) {
        if (newContent == null || newContent.isEmpty() || newContent.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.content = newContent;
    }

    public void updatePostCategory(Category newCategory) {
        if (newCategory == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.category = newCategory;
    }

    public void updatePostLocation(String newLocation) {
        if (newLocation == null || newLocation.isEmpty() || newLocation.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.location = newLocation;
    }

    public void deletePost() {
        this.deleted = true;
    }

}