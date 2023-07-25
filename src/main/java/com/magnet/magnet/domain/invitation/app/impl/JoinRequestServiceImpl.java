package com.magnet.magnet.domain.invitation.app.impl;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.invitation.app.JoinRequestService;
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

import static com.magnet.magnet.domain.club.app.impl.ClubServiceImpl.MAX_CLUB_JOIN_COUNT;

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
        Club findClub = getClubByInvitationCodeAndDeletedFalse(invitationCode);
        User currentUser = getUserByEmail(email);

        // 가입하려는 유저가 가입한 동아리 개수가 MAX_CLUB_JOIN_COUNT 이하인지 체크
        validateActiveClubUserCountForUser(currentUser);

        validateUserAndRequestNotExist(findClub, currentUser);

        joinRequestRepo.save(JoinRequest.builder()
                .club(findClub)
                .user(currentUser)
                .status(JoinRequest.Status.WAITING)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseJoinRequest> getJoinRequestList(Long clubId, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        return joinRequestRepo.findAllByClubAndStatus(findClub, JoinRequest.Status.WAITING)
                .stream()
                .map(joinRequest -> ResponseJoinRequest.builder()
                        .id(joinRequest.getId())
                        .clubId(joinRequest.getClub().getId())
                        .clubTitle(joinRequest.getClub().getTitle())
                        .userId(joinRequest.getUser().getId())
                        .userNickname(joinRequest.getUser().getDefaultNickname())
                        .createdDate(joinRequest.getCreatedDate())
                        .modifiedDate(joinRequest.getModifiedDate())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void acceptJoinRequest(Long joinRequestId, String email) {
        JoinRequest findRequest = getJoinRequestByIdAndStatusWaiting(joinRequestId);

        Club findClub = findRequest.getClub();
        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        findRequest.acceptRequest();

        String userDefaultNickname = findRequest.getUser().getDefaultNickname();

        // 요청한 유저를 동아리에 추가
        clubUserRepo.save(ClubUser.builder()
                .club(findRequest.getClub())
                .user(findRequest.getUser())
                .nickname(userDefaultNickname)
                .role(ClubUser.Role.USER)
                .build());
    }

    @Override
    @Transactional
    public void rejectJoinRequest(Long joinRequestId, String email) {
        JoinRequest request = getJoinRequestByIdAndStatusWaiting(joinRequestId);
        User currentUser = getUserByEmail(email);

        validateAdminRole(request.getClub(), currentUser);

        request.rejectRequest();
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Club getClubByInvitationCodeAndDeletedFalse(String invitationCode) {
        return clubRepo.findByInvitationInvitationCodeAndDeletedFalse(invitationCode)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private Club getClubByIdAndDeletedFalse(Long clubId) {
        return clubRepo.findByIdAndDeletedFalse(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private void validateAdminRole(Club club, User user) {
        if (club.getUserRole(user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }
    }

    private JoinRequest getJoinRequestByIdAndStatusWaiting(Long joinRequestId) {
        return joinRequestRepo.findByIdAndStatus(joinRequestId, JoinRequest.Status.WAITING)
                .orElseThrow(() -> new CustomException(ErrorCode.JOIN_REQUEST_NOT_FOUND));
    }

    private void validateActiveClubUserCountForUser(User user) {
        int joinedClubCount = clubUserRepo.countByUserAndDeletedFalse(user);

        if (joinedClubCount >= MAX_CLUB_JOIN_COUNT) {
            throw new CustomException(ErrorCode.CLUB_LIMIT_EXCEED);
        }
    }

    private void validateUserAndRequestNotExist(Club club, User user) {
        boolean isUserInClub = clubUserRepo.existsByClubAndUserAndDeletedFalse(club, user);
        boolean isRequestExist = joinRequestRepo.existsByClubAndUserAndStatus(club, user, JoinRequest.Status.WAITING);

        if (isUserInClub) {
            throw new CustomException(ErrorCode.CLUB_USER_ALREADY_EXIST);
        }

        if (isRequestExist) {
            throw new CustomException(ErrorCode.JOIN_REQUEST_ALREADY_EXIST);
        }
    }

}