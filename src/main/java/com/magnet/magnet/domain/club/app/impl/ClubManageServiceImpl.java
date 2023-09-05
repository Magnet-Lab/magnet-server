package com.magnet.magnet.domain.club.app.impl;

import com.magnet.magnet.domain.club.app.ClubManageService;
import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.club.entity.ClubUser;
import com.magnet.magnet.domain.club.dto.request.RequestManagement;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateNickname;
import com.magnet.magnet.domain.club.dto.response.ResponseUserInClub;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.entity.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubManageServiceImpl implements ClubManageService {

    private final ClubRepo clubRepo;

    private final UserRepo userRepo;

    private final ClubUserRepo clubUserRepo;

    @Override
    @Transactional
    public void setUserAsAdmin(RequestManagement dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByClubAndEmail(findClub, email);

        // 관리자가 아닌 경우 예외 처리
        validateAdminRole(findClub, currentUser);

        // 관리자로 만들려는 유저 찾기
        User targetUser = getUserById(dto.getUserId());

        // 관계 찾고 관리자로 업데이트
        ClubUser targetClubUser = getClubUserByClubAndUserAndDeletedFalse(findClub, targetUser);
        targetClubUser.updateRoleToAdmin();
    }

    @Override
    @Transactional
    public void setUserAsUser(RequestManagement dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByClubAndEmail(findClub, email);

        validateAdminRole(findClub, currentUser);

        User targetUser = getUserById(dto.getUserId());

        ClubUser targetClubUser = getClubUserByClubAndUserAndDeletedFalse(findClub, targetUser);
        targetClubUser.updateRoleToUser();
    }

    @Override
    @Transactional
    public void deleteUser(RequestManagement dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByClubAndEmail(findClub, email);

        validateAdminRole(findClub, currentUser);

        User targetUser = getUserById(dto.getUserId());

        ClubUser targetClubUser = getClubUserByClubAndUserAndDeletedFalse(findClub, targetUser);
        targetClubUser.deleteClubUser();
    }

    @Override
    @Transactional
    public void updateClubNickname(RequestUpdateNickname dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(dto.getClubId());
        User currentUser = getUserByClubAndEmail(findClub, email);

        ClubUser targetClubUser = getClubUserByClubAndUserAndDeletedFalse(findClub, currentUser);

        targetClubUser.updateNickname(dto.getNickname());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResponseUserInClub> getUsersInClub(Long clubId, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        User currentUser = getUserByClubAndEmail(findClub, email);

        validateAdminRole(findClub, currentUser);

        return clubUserRepo.findAllByClubAndDeletedFalse(findClub).stream()
                .map(clubUser -> ResponseUserInClub.builder()
                        .userId(clubUser.getUser().getId())
                        .clubNickname(clubUser.getNickname())
                        .defaultNickname(clubUser.getUser().getDefaultNickname())
                        .role(clubUser.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    private User getUserByClubAndEmail(Club club, String email) {
        return club.getClubUsers().stream().map(ClubUser::getUser).filter(user -> user.getEmail().equals(email)).findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Club getClubByIdAndDeletedFalse(Long clubId) {
        return clubRepo.findByIdAndDeletedFalse(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private ClubUser getClubUserByClubAndUserAndDeletedFalse(Club club, User user) {
        return clubUserRepo.findByClubAndUserAndDeletedFalse(club, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));
    }

    private void validateAdminRole(Club club, User user) {
        if (club.getUserRole(user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }
    }

}