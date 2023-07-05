package com.magnet.magnet.domain.invitation.app;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.invitation.dao.JoinRequestRepo;
import com.magnet.magnet.domain.invitation.domain.JoinRequest;
import com.magnet.magnet.domain.invitation.dto.response.ResponseJoinRequest;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JoinRequestServiceImpl implements JoinRequestService {

    private final JoinRequestRepo joinRequestRepo;

    private final ClubRepo clubRepo;

    private final UserRepo userRepo;

    private final ClubUserRepo clubUserRepo;

    @Override
    @Transactional
    public void createJoinRequest(String invitationCode, String email) {
        Club club = clubRepo.findByInvitationInvitationCode(invitationCode)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 요청한 유저가 동아리에 속해있는지 확인
        clubUserRepo.findByClubAndUserAndDeleted(club, user, false)
                .ifPresent(clubUser -> {
                    throw new CustomException(ErrorCode.CLUB_USER_ALREADY_EXIST);
                });

        // 요청한 유저가 이미 요청을 보냈는지 확인
        joinRequestRepo.findByClubAndUserAndStatus(club, user, JoinRequest.Status.WAITING)
                .ifPresent(joinRequest -> {
                    throw new CustomException(ErrorCode.JOIN_REQUEST_ALREADY_EXIST);
                });

        joinRequestRepo.save(JoinRequest.builder()
                        .club(club)
                        .user(user)
                        .status(JoinRequest.Status.WAITING)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseJoinRequest> getJoinRequestList(Long clubId, String email) {
        Club findClub = clubRepo.findByIdAndDeleted(clubId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        return joinRequestRepo.findAllByClubAndStatus(findClub, JoinRequest.Status.WAITING)
                .stream()
                .map(joinRequest -> ResponseJoinRequest.builder()
                        .id(joinRequest.getId())
                        .clubId(joinRequest.getClub().getId())
                        .clubTitle(joinRequest.getClub().getTitle())
                        .userId(joinRequest.getUser().getId())
                        .userNickname(joinRequest.getUser().getNickname())
                        .createdDate(joinRequest.getCreatedDate())
                        .modifiedDate(joinRequest.getModifiedDate())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void acceptJoinRequest(Long joinRequestId) {
        JoinRequest request = joinRequestRepo.findById(joinRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

        request.acceptRequest();

        // 요청한 유저를 동아리에 추가
        clubUserRepo.save(ClubUser.builder()
                .club(request.getClub())
                .user(request.getUser())
                .role(ClubUser.Role.USER)
                .build());

        joinRequestRepo.save(request);
    }

    @Override
    @Transactional
    public void rejectJoinRequest(Long joinRequestId) {
        JoinRequest request = joinRequestRepo.findById(joinRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_REQUEST_NOT_FOUND));

        request.rejectRequest();

        joinRequestRepo.save(request);
    }

    private ClubUser.Role getClubUserRole(Club club, User user) {
        return clubUserRepo.findByClubAndUserAndDeleted(club, user, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND))
                .getRole();
    }

}