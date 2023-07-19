package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.dto.request.RequestManagement;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateNickname;

public interface ClubManageService {

    void setUserAsAdmin(RequestManagement dto, String email);

    void setUserAsUser(RequestManagement dto, String email);

    void deleteUser(RequestManagement dto, String email);

    void updateClubNickname(RequestUpdateNickname dto, String email);

}