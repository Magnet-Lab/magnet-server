package com.magnet.magnet.domain.post.category.domain;

import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.global.common.BaseTime;
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
public class Category extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @Builder.Default
    private boolean deleted = false;

    public void updateCategoryTitle(String newTitle) {
        this.title = newTitle;
    }

    public void updateCategoryDescription(String newDescription) {
        this.description = newDescription;
    }

    public void deleteCategory() {
        this.deleted = true;
    }

}