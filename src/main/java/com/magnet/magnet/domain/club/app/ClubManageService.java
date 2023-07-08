package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.dto.request.RequestManagement;

public interface ClubManageService {

    void setUserAsAdmin(RequestManagement dto, String email);

    void setUserAsUser(RequestManagement dto, String email);

    void deleteUser(RequestManagement dto, String email);

}