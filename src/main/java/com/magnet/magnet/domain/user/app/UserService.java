package com.magnet.magnet.domain.user.app;

import com.magnet.magnet.domain.user.dto.response.ResponseUser;

import java.security.Principal;

public interface UserService {

    ResponseUser myInfo(Principal principal);

}