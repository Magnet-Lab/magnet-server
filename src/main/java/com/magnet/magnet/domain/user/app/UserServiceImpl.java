package com.magnet.magnet.domain.user.app;

import com.magnet.magnet.domain.user.dao.UserRepository;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.domain.user.dto.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public ResponseUser myInfo(Principal principal) {
        Optional<User> optionalUser = userRepository.findByEmail(principal.getName());

        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        return ResponseUser.builder()
                .id(optionalUser.get().getId())
                .email(optionalUser.get().getEmail())
                .nickname(optionalUser.get().getNickname())
                .registrationId(optionalUser.get().getRegistrationId())
                .uid(optionalUser.get().getUid())
                .build();
    }
}