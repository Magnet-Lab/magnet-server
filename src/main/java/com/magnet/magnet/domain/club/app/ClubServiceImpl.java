package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.domain.Club;
import com.magnet.magnet.domain.club.domain.ClubUser;
import com.magnet.magnet.domain.club.dto.request.RequestCreateClub;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateClub;
import com.magnet.magnet.domain.club.dto.response.ResponseClub;
import com.magnet.magnet.domain.invitation.dao.InvitationRepo;
import com.magnet.magnet.domain.invitation.dao.JoinRequestRepo;
import com.magnet.magnet.domain.invitation.domain.Invitation;
import com.magnet.magnet.domain.invitation.domain.JoinRequest;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import com.magnet.magnet.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {

    private final ClubRepo clubRepo;

    private final ClubUserRepo clubUserRepo;

    private final UserRepo userRepo;

    private final InvitationRepo invitationRepo;

    private final JoinRequestRepo joinRequestRepo;

    @Override
    @Transactional
    public ResponseClub createClub(RequestCreateClub dto, String email) {

        // TODO: 생성하려는 유저가 가입한 동아리가 3개 이하인지 체크\

        // 동아리 초대 코드 중복 체크 및 생성
        Invitation savedInvitation;
        while (true) {
            String invitationCode = UUID.randomUUID().toString().substring(0, 8);
            if (invitationRepo.findByInvitationCode(invitationCode).isEmpty()) {
                savedInvitation = invitationRepo.save(Invitation.builder()
                        .invitationCode(invitationCode)
                        .build());
                break;
            }
        }

        // 동아리 생성
        Club createClub = clubRepo.save(Club.builder()
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .invitation(savedInvitation)
                        .deleted(false)
                        .build());

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관계 생성
        clubUserRepo.save(ClubUser.builder()
                .club(createClub)
                .user(user)
                .role(ClubUser.Role.ADMIN) // 동아리 생성한 유저는 관리자
                .deleted(false)
                .build());

        // 생성한 동아리 정보 반환
        return ResponseClub.builder()
                .id(createClub.getId())
                .title(createClub.getTitle())
                .description(createClub.getDescription())
                .inviteCode(createClub.getInvitation().getInvitationCode())
                .createdDate(createClub.getCreatedDate())
                .modifiedDate(createClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub updateClub(Long clubId, RequestUpdateClub dto, String email) {
        // 동아리 찾기
        Club findClub = clubRepo.findByIdAndDeleted(clubId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 아닌 경우 예외 처리
        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        // 동아리 수정 및 저장
        findClub.updateClub(dto.getTitle(), dto.getDescription());
        Club updateClub = clubRepo.save(findClub);

        // 수정한 동아리 정보 반환
        return ResponseClub.builder()
                .id(updateClub.getId())
                .title(updateClub.getTitle())
                .description(updateClub.getDescription())
                .inviteCode(updateClub.getInvitation().getInvitationCode())
                .createdDate(updateClub.getCreatedDate())
                .modifiedDate(updateClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub deleteClub(Long clubId, String email) {
        // 동아리 찾기
        Club findClub = clubRepo.findByIdAndDeleted(clubId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 관리자가 아닌 경우 예외 처리
        if (getClubUserRole(findClub, user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }

        // 동아리 삭제 처리 및 저장
        findClub.deleteClub();
        Club deletedClub = clubRepo.save(findClub);

        // 관계 삭제 처리 및 저장
        List<ClubUser> clubUserList = clubUserRepo.findAllByClubAndDeleted(findClub, false);
        for (ClubUser clubUser : clubUserList) {
            clubUser.deleteClubUser();
            clubUserRepo.save(clubUser);
        }

        // 가입 요청 리젝 처리 및 저장
        List<JoinRequest> joinRequestList = joinRequestRepo.findAllByClubAndStatus(findClub, JoinRequest.Status.WAITING);
        for (JoinRequest joinRequest : joinRequestList) {
            joinRequest.rejectRequest();
            joinRequestRepo.save(joinRequest);
        }

        // 삭제한 동아리 정보 반환
        return ResponseClub.builder()
                .id(deletedClub.getId())
                .title(deletedClub.getTitle())
                .description(deletedClub.getDescription())
                .inviteCode(deletedClub.getInvitation().getInvitationCode())
                .createdDate(deletedClub.getCreatedDate())
                .modifiedDate(deletedClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub getClub(Long clubId) {
        // 동아리 찾기
        Club club = clubRepo.findByIdAndDeleted(clubId, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));

        // 동아리 정보 반환
        return ResponseClub.builder()
                .id(club.getId())
                .title(club.getTitle())
                .description(club.getDescription())
                .inviteCode(club.getInvitation().getInvitationCode())
                .createdDate(club.getCreatedDate())
                .modifiedDate(club.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public List<ResponseClub> getMyClubList(String email) {
        // 로그인 된 유저 찾기
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 유저가 속한 동아리 목록 찾기
        List<ClubUser> clubUserList = clubUserRepo.findByUserAndDeleted(user, false);

        // 동아리 목록 List<ResponseClub> 형태로 반환
        return clubUserList.stream()
                .map(clubUser -> ResponseClub.builder()
                        .id(clubUser.getClub().getId())
                        .title(clubUser.getClub().getTitle())
                        .description(clubUser.getClub().getDescription())
                        .inviteCode(clubUser.getClub().getInvitation().getInvitationCode())
                        .createdDate(clubUser.getClub().getCreatedDate())
                        .modifiedDate(clubUser.getClub().getModifiedDate())
                        .build())
                .toList();
    }

    // 동아리와 유저, 관계를 찾고 관계의 role 반환하는 메소드
    private ClubUser.Role getClubUserRole(Club club, User user) {
        return clubUserRepo.findByClubAndUserAndDeleted(club, user, false)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND))
                .getRole();
    }

}