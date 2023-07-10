package com.magnet.magnet.domain.invitation.app;

import com.magnet.magnet.domain.invitation.dto.response.ResponseJoinRequest;

import java.util.List;

public interface JoinRequestService {

    void createJoinRequest(String invitationCode, String email);

    List<ResponseJoinRequest> getJoinRequestList(Long clubId, String email);

    void acceptJoinRequest(Long joinRequestId, String email);

    void rejectJoinRequest(Long joinRequestId, String email);

}