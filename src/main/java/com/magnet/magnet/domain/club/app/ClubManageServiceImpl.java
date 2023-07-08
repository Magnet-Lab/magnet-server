package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.club.dto.request.RequestManagement;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClubManageServiceImpl implements ClubManageService {

    private final ClubRepo clubRepo;

    private final UserRepo userRepo;

    private final ClubUserRepo clubUserRepo;

    @Override
    public void setUserAsAdmin(RequestManagement dto, String email) {
        Club findClub = clubRepo.findById(dto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 아닌 경우 예외 처리
        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        // 관리자로 만들려는 유저 찾기
        User findUser = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관계 찾기
        ClubUser findClubUser = clubUserRepo.findByClubAndUserAndDeleted(findClub, findUser, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));

        findClubUser.updateRoleToAdmin();
        clubUserRepo.save(findClubUser);
    }

    @Override
    public void setUserAsUser(RequestManagement dto, String email) {
        Club findClub = clubRepo.findById(dto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 아닌 경우 예외 처리
        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        User findUser = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ClubUser findClubUser = clubUserRepo.findByClubAndUserAndDeleted(findClub, findUser, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));

        findClubUser.updateRoleToUser();
        clubUserRepo.save(findClubUser);
    }

    @Override
    public void deleteUser(RequestManagement dto, String email) {
        Club findClub = clubRepo.findById(dto.getClubId())
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 아닌 경우 예외 처리
        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        User findUser = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ClubUser findClubUser = clubUserRepo.findByClubAndUserAndDeleted(findClub, findUser, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));

        findClubUser.deleteClubUser();
        clubUserRepo.save(findClubUser);
    }

    // 동아리와 유저, 관계를 찾고 관계의 role 반환하는 메소드
    private ClubUser.Role getClubUserRole(Club club, User user) {
        return clubUserRepo.findByClubAndUserAndDeleted(club, user, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND))
                .getRole();
    }


}