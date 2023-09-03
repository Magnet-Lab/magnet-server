package com.magnet.magnet.domain.post.category.domain;

import com.magnet.magnet.domain.invitation.domain.JoinRequest;
import com.magnet.magnet.domain.user.domain.User;
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
public class Subscribe extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Subscribe.Status status = Subscribe.Status.SUBSCRIBE;

    public enum Status {
        SUBSCRIBE, UNSUBSCRIBE
    }

    public void unSubscribe() {
        this.status = Status.UNSUBSCRIBE;
    }

}