package com.magnet.magnet.domain.post.category.entity;

import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.post.content.entity.Post;
import com.magnet.magnet.global.common.BaseTime;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Builder
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Post> postList;

    @Enumerated(EnumType.STRING)
    private Category.Role permissionRange;

    public enum Role {
        ADMIN, USER
    }

    @Builder.Default
    private boolean deleted = false;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Category category) {
            return this.id.equals(category.getId());
        }
        return false;
    }

    public void updateCategoryTitle(String newTitle) {
        if (newTitle == null || newTitle.isEmpty() || newTitle.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.title = newTitle;
    }

    public void updateCategoryDescription(String newDescription) {
        if (newDescription == null || newDescription.isEmpty() || newDescription.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.description = newDescription;
    }

    public void updateCategoryAccessRange(Category.Role newPermissionRange) {
        if (newPermissionRange == null) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER);
        }
        this.permissionRange = newPermissionRange;
    }

    public void deleteCategory() {
        this.deleted = true;
    }

}