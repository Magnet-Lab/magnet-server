package com.magnet.magnet.domain.club.app;

import com.magnet.magnet.domain.club.app.impl.ClubServiceImpl;
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
import com.magnet.magnet.domain.post.category.dao.CategoryRepo;
import com.magnet.magnet.domain.post.category.domain.Category;
import com.magnet.magnet.domain.user.dao.UserRepo;
import com.magnet.magnet.domain.user.domain.User;
import com.magnet.magnet.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @InjectMocks
    private ClubServiceImpl clubService;

    @Mock
    private ClubRepo clubRepo;

    @Mock
    private InvitationRepo invitationRepo;

    @Mock
    private ClubUserRepo clubUserRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private JoinRequestRepo joinRequestRepo;

    private Long testClubId;

    private Long testUserId;

    private User testUser;

    private Invitation testInvitation;

    private Club testClub;

    private ClubUser testClubUser;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testClubId = 1L;

        testUser = User.builder()
                .id(testUserId)
                .email("kim@gmail.com")
                .defaultNickname("kim")
                .registrationId("google")
                .uid("1234")
                .build();

        testInvitation = Invitation.builder()
                .id(1L)
                .invitationCode("1234")
                .build();

        testClub = Club.builder()
                .id(testClubId)
                .title("테스트 동아리")
                .description("테스트 동아리입니다.")
                .invitation(testInvitation)
                .clubUsers(Collections.singletonList(ClubUser.builder()
                        .user(testUser)
                        .role(ClubUser.Role.ADMIN)
                        .deleted(false)
                        .build()))
                .deleted(false)
                .build();

        testClubUser = ClubUser.builder()
                .club(testClub)
                .user(testUser)
                .role(ClubUser.Role.ADMIN)
                .deleted(false)
                .build();

        testCategory = Category.builder()
                .club(testClub)
                .title("공지사항")
                .description("공지사항입니다.")
                .build();
    }

    @Test
    @DisplayName("동아리 생성 성공 시 동아리 정보가 올바르게 저장되는지 확인")
    void successToCreateClub() {
        // given
        RequestCreateClub createDto = RequestCreateClub.builder()
                .title("개발 동아리")
                .description("재미있는 개발 동아리입니다.")
                .build();

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(testUser));
        when(clubUserRepo.countByUserAndDeletedFalse(testUser)).thenReturn(0);
        when(invitationRepo.findByInvitationCode(any())).thenReturn(Optional.empty());
        when(invitationRepo.save(any())).thenReturn(testInvitation);
        when(clubRepo.save(any())).thenReturn(Club.builder()
                .title(createDto.getTitle())
                .description(createDto.getDescription())
                .invitation(testInvitation)
                .deleted(false)
                .build()
        );
        when(categoryRepo.save(any())).thenReturn(testCategory);
        when(clubUserRepo.save(any())).thenReturn(ClubUser.builder()
                .club(testClub)
                .user(testUser)
                .role(ClubUser.Role.ADMIN)
                .deleted(false)
                .build());

        // when
        ResponseClub responseClub = clubService.createClub(createDto, testUser.getEmail());

        // then
        assertEquals(createDto.getTitle(), responseClub.getTitle());
        assertEquals(createDto.getDescription(), responseClub.getDescription());
        assertEquals(testInvitation.getInvitationCode(), responseClub.getInvitationCode());
    }

    @Test
    @DisplayName("동아리 수정 성공 시 동아리 정보가 올바르게 수정되는지 확인")
    void successToUpdateClub() {
        // given
        RequestUpdateClub updateDto = RequestUpdateClub.builder()
                .title("수정된 동아리")
                .description("수정된 동아리입니다.")
                .build();

        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubRepo.findByIdAndDeletedFalse(testClubId)).thenReturn(Optional.of(testClub));

        // when
        ResponseClub responseClub = clubService.updateClub(testClubId, updateDto, testUser.getEmail());

        // then
        assertEquals(updateDto.getTitle(), responseClub.getTitle());
        assertEquals(updateDto.getDescription(), responseClub.getDescription());
        assertEquals(testInvitation.getInvitationCode(), responseClub.getInvitationCode());
    }

    @Test
    @DisplayName("동아리 삭제 성공 시 동아리 정보가 올바르게 삭제되는지 확인")
    void successToDeleteClub() {
        // given
        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubRepo.findByIdAndDeletedFalse(testClubId)).thenReturn(Optional.of(testClub));

        when(joinRequestRepo.findAllByClubAndStatus(testClub, JoinRequest.Status.WAITING)).thenReturn(Collections.emptyList());

        // when
        ResponseClub responseClub = clubService.deleteClub(testClubId, testUser.getEmail());

        // then
        assertEquals(testClub.getTitle(), responseClub.getTitle());
        assertEquals(testClub.getDescription(), responseClub.getDescription());
        assertEquals(testInvitation.getInvitationCode(), responseClub.getInvitationCode());
        assertTrue(testClub.isDeleted());
    }

    @Test
    @DisplayName("동아리 조회 성공 시 동아리 정보가 올바르게 조회되는지 확인")
    void successToGetClub() {
        // given
        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubRepo.findByIdAndDeletedFalse(testClubId)).thenReturn(Optional.of(testClub));

        when(clubUserRepo.findByClubAndUserAndDeletedFalse(testClub, testUser)).thenReturn(Optional.of(testClubUser));

        // when
        ResponseClub responseClub = clubService.getClub(testClubId, testUser.getEmail());

        // then
        assertEquals(testClub.getTitle(), responseClub.getTitle());
        assertEquals(testClub.getDescription(), responseClub.getDescription());
        assertEquals(testInvitation.getInvitationCode(), responseClub.getInvitationCode());
    }

    @Test
    @DisplayName("나의 동아리 목록 조회 성공 시 동아리 목록이 올바르게 조회되는지 확인")
    void successToGetMyClubList() {
        // given
        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubUserRepo.findAllByUserAndDeletedFalse(testUser)).thenReturn(Collections.singletonList(testClubUser));

        // when
        List<ResponseClub> responseClubList = clubService.getMyClubList(testUser.getEmail());

        // then
        assertEquals(testClub.getTitle(), responseClubList.get(0).getTitle());
        assertEquals(testClub.getDescription(), responseClubList.get(0).getDescription());
        assertEquals(testInvitation.getInvitationCode(), responseClubList.get(0).getInvitationCode());
    }

    @Test
    @DisplayName("동아리 가입 개수가 MAX_CLUB_JOIN_COUNT 이상일 경우 동아리 생성 실패하는지 확인")
    void failToCreateClubWhenClubUserCountIsOverThree() {
        // given
        RequestCreateClub createDto = RequestCreateClub.builder()
                .title("개발 동아리")
                .description("재미있는 개발 동아리입니다.")
                .build();

        when(userRepo.findByEmail(any())).thenReturn(Optional.of(testUser));
        when(clubUserRepo.countByUserAndDeletedFalse(testUser)).thenReturn(3);

        // when
        assertThrows(CustomException.class, () -> clubService.createClub(createDto, testUser.getEmail()));

        // then
        verify(clubRepo, never()).save(any());
    }

    @Test
    @DisplayName("관리자가 아닌 경우 동아리 수정 실패하는지 확인")
    void failToUpdateClubWhenUserIsNotAdmin() {
        // given
        Club failTestClub = Club.builder()
                .id(testClubId)
                .title("테스트 동아리")
                .description("테스트 동아리입니다.")
                .invitation(testInvitation)
                .clubUsers(Collections.singletonList(ClubUser.builder()
                        .user(testUser)
                        .role(ClubUser.Role.USER)
                        .deleted(false)
                        .build()))
                .deleted(false)
                .build();

        RequestUpdateClub updateDto = RequestUpdateClub.builder()
                .title("수정된 동아리")
                .description("수정된 동아리입니다.")
                .build();

        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubRepo.findByIdAndDeletedFalse(testClubId)).thenReturn(Optional.of(failTestClub));

        // when
        assertThrows(CustomException.class, () -> clubService.updateClub(testClubId, updateDto, testUser.getEmail()));

        // then
        verify(clubRepo, never()).save(any());
    }

    @Test
    @DisplayName("관리자가 아닌 경우 동아리 삭제 실패하는지 확인")
    void failToDeleteClubWhenUserIsNotAdmin() {
        // given
        Club failTestClub = Club.builder()
                .id(testClubId)
                .title("테스트 동아리")
                .description("테스트 동아리입니다.")
                .invitation(testInvitation)
                .clubUsers(Collections.singletonList(ClubUser.builder()
                        .user(testUser)
                        .role(ClubUser.Role.USER)
                        .deleted(false)
                        .build()))
                .deleted(false)
                .build();

        when(userRepo.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(clubRepo.findByIdAndDeletedFalse(testClubId)).thenReturn(Optional.of(failTestClub));

        // when
        assertThrows(CustomException.class, () -> clubService.deleteClub(testClubId, testUser.getEmail()));

        // then
        verify(clubRepo, never()).save(any());
    }

}