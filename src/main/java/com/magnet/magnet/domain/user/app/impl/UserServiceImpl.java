package com.magnet.magnet.domain.user.app.impl;

import com.magnet.magnet.domain.user.app.UserService;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.entity.User;
import com.magnet.magnet.domain.user.dto.response.ResponseUser;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    @Transactional(readOnly = true)
    public ResponseUser myInfo(Principal principal) {
        User user = getUserByEmail(principal.getName());

        return ResponseUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .defaultNickname(user.getDefaultNickname())
                .registrationId(user.getRegistrationId())
                .uid(user.getUid())
                .build();
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}