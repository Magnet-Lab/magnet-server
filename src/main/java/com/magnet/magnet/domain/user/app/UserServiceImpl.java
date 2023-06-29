package com.magnet.magnet.domain.user.app;

import com.magnet.magnet.domain.user.dao.UserRepository;
import com.magnet.magnet.domain.user.domain.User;
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

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseUser myInfo(Principal principal) {
        User user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return ResponseUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .registrationId(user.getRegistrationId())
                .uid(user.getUid())
                .build();
    }
}