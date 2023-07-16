package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.dto.request.RequestCreateClub;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateClub;
import com.magnet.magnet.domain.club.dto.response.ResponseClub;

import java.util.List;

public interface ClubService {

    ResponseClub createClub(RequestCreateClub dto, String email);

    ResponseClub updateClub(Long id, RequestUpdateClub dto, String email);

    ResponseClub deleteClub(Long id, String email);

    ResponseClub getClub(Long id, String email);

    List<ResponseClub> getMyClubList(String email);

}