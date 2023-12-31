package com.magnet.magnet.domain.club.app.impl;

import com.magnet.magnet.domain.club.app.ClubService;
import com.magnet.magnet.domain.club.dao.ClubRepo;
import com.magnet.magnet.domain.club.dao.ClubUserRepo;
import com.magnet.magnet.domain.club.entity.Club;
import com.magnet.magnet.domain.club.entity.ClubUser;
import com.magnet.magnet.domain.club.dto.request.RequestCreateClub;
import com.magnet.magnet.domain.club.dto.request.RequestUpdateClub;
import com.magnet.magnet.domain.club.dto.response.ResponseClub;
import com.magnet.magnet.domain.invitation.dao.InvitationRepo;
import com.magnet.magnet.domain.invitation.dao.JoinRequestRepo;
import com.magnet.magnet.domain.invitation.entity.Invitation;
import com.magnet.magnet.domain.invitation.entity.JoinRequest;
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.entity.Category;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.entity.User;
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

    private final CategoryRepo categoryRepo;

    public static final Integer MAX_CLUB_JOIN_COUNT = 3;

    @Override
    @Transactional
    public ResponseClub createClub(RequestCreateClub dto, String email) {
        User currentUser = getUserByEmail(email);

        // 생성하려는 유저가 가입한 동아리 개수가 MAX_CLUB_JOIN_COUNT 이하인지 체크
        validateActiveClubUserCountForUser(currentUser);

        // 동아리 초대 코드 중복 체크 및 생성
        String invitationCode = generateUniqueInvitationCode();

        Invitation createInvitation = invitationRepo.save(Invitation.builder()
                .invitationCode(invitationCode)
                .build());

        // 동아리 생성
        Club createClub = clubRepo.save(Club.builder()
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .invitation(createInvitation)
                        .deleted(false)
                        .build());

        // 동아리 내 기본 카테고리인 공지사항 생성
        categoryRepo.save(Category.builder()
                        .club(createClub)
                        .title("공지사항")
                        .description("공지사항입니다.")
                        .permissionRange(Category.Role.ADMIN)
                        .build());

        // 관계 생성
        createAdminClubUser(createClub, currentUser);

        // 생성한 동아리 정보 반환
        return ResponseClub.builder()
                .id(createClub.getId())
                .title(createClub.getTitle())
                .description(createClub.getDescription())
                .invitationCode(createClub.getInvitation().getInvitationCode())
                .myRole(String.valueOf(ClubUser.Role.ADMIN))
                .createdDate(createClub.getCreatedDate())
                .modifiedDate(createClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub updateClub(Long clubId, RequestUpdateClub dto, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        User currentUser = getUserByEmail(email);

        // 관리자가 아닌 경우 예외 처리
        validateAdminRole(findClub, currentUser);

        findClub.updateClubTitle(dto.getTitle());
        findClub.updateClubDescription(dto.getDescription());

        return ResponseClub.builder()
                .id(findClub.getId())
                .title(findClub.getTitle())
                .description(findClub.getDescription())
                .invitationCode(findClub.getInvitation().getInvitationCode())
                .myRole(String.valueOf(ClubUser.Role.ADMIN))
                .createdDate(findClub.getCreatedDate())
                .modifiedDate(findClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub deleteClub(Long clubId, String email) {
        Club findClub = getClubByIdAndDeletedFalse(clubId);
        User currentUser = getUserByEmail(email);

        validateAdminRole(findClub, currentUser);

        findClub.deleteClub();

        // 관계 삭제 처리
        deleteClubUsers(findClub);

        // 가입 요청 리젝 처리
        rejectJoinRequests(findClub);

        return ResponseClub.builder()
                .id(findClub.getId())
                .title(findClub.getTitle())
                .description(findClub.getDescription())
                .invitationCode(findClub.getInvitation().getInvitationCode())
                .myRole(String.valueOf(ClubUser.Role.ADMIN))
                .createdDate(findClub.getCreatedDate())
                .modifiedDate(findClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public ResponseClub getClub(Long clubId, String email) {
        Club findClub = getClubByIdAndUserEmailAndDeletedFalse(clubId, email);
        ClubUser myClubUser = getClubUserByClubAndUserEmailAndDeletedFalse(findClub, email);

        String myNickname = myClubUser.getNickname();
        String myRole = String.valueOf(myClubUser.getRole());

        return ResponseClub.builder()
                .id(findClub.getId())
                .title(findClub.getTitle())
                .description(findClub.getDescription())
                .invitationCode(findClub.getInvitation().getInvitationCode())
                .myNickname(myNickname)
                .myRole(myRole)
                .createdDate(findClub.getCreatedDate())
                .modifiedDate(findClub.getModifiedDate())
                .build();
    }

    @Override
    @Transactional
    public List<ResponseClub> getMyClubList(String email) {
        User currentUser = getUserByEmail(email);

        List<ClubUser> userClubList = clubUserRepo.findAllByUserAndDeletedFalse(currentUser);

        // 동아리 목록 List<ResponseClub> 형태로 반환
        return userClubList.stream()
                .map(clubUser -> ResponseClub.builder()
                        .id(clubUser.getClub().getId())
                        .title(clubUser.getClub().getTitle())
                        .description(clubUser.getClub().getDescription())
                        .invitationCode(clubUser.getClub().getInvitation().getInvitationCode())
                        .myNickname(clubUser.getNickname())
                        .myRole(String.valueOf(clubUser.getRole()))
                        .createdDate(clubUser.getClub().getCreatedDate())
                        .modifiedDate(clubUser.getClub().getModifiedDate())
                        .build())
                .toList();
    }

    private String generateUniqueInvitationCode() {
        String invitationCode;
        do {
            invitationCode = UUID.randomUUID().toString().substring(0, 8);
        } while (invitationRepo.findByInvitationCode(invitationCode).isPresent());
        return invitationCode;
    }

    private User getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Club getClubByIdAndDeletedFalse(Long clubId) {
        return clubRepo.findByIdAndDeletedFalse(clubId)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private Club getClubByIdAndUserEmailAndDeletedFalse(Long clubId, String email) {
        return clubRepo.findByIdAndClubUsers_User_EmailAndDeletedFalse(clubId, email)
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_NOT_FOUND));
    }

    private void validateAdminRole(Club club, User user) {
        if (club.getUserRole(user) != ClubUser.Role.ADMIN) {
            throw new CustomException(ErrorCode.CLUB_USER_NOT_FOUND);
        }
    }

    private void validateActiveClubUserCountForUser(User user) {
        int joinedClubCount = clubUserRepo.countByUserAndDeletedFalse(user);

        if (joinedClubCount >= MAX_CLUB_JOIN_COUNT) {
            throw new CustomException(ErrorCode.CLUB_LIMIT_EXCEED);
        }
    }

    private void deleteClubUsers(Club club) {
        List<ClubUser> clubUsers = clubUserRepo.findAllByClubAndDeletedFalse(club);
        clubUsers.forEach(ClubUser::deleteClubUser);
        clubUserRepo.saveAll(clubUsers);
    }

    private void rejectJoinRequests(Club club) {
        List<JoinRequest> joinRequests = joinRequestRepo.findAllByClubAndStatus(club, JoinRequest.Status.WAITING);
        joinRequests.forEach(JoinRequest::rejectRequest);
        joinRequestRepo.saveAll(joinRequests);
    }

    private void createAdminClubUser(Club club, User user) {
        clubUserRepo.save(ClubUser.builder()
                .club(club)
                .user(user)
                .role(ClubUser.Role.ADMIN)
                .nickname(user.getDefaultNickname())
                .deleted(false)
                .build());
    }

    private ClubUser getClubUserByClubAndUserEmailAndDeletedFalse(Club club, String email) {
        return club.getClubUsers().stream()
                .filter(clubUser -> clubUser.getUser().getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.CLUB_USER_NOT_FOUND));
    }

}