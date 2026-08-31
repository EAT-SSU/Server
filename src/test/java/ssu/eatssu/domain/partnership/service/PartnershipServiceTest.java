package ssu.eatssu.domain.partnership.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.request.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.entity.Partnership;
import ssu.eatssu.domain.partnership.entity.PartnershipLike;
import ssu.eatssu.domain.partnership.entity.PartnershipRestaurant;
import ssu.eatssu.domain.partnership.persistence.PartnershipLikeRepository;
import ssu.eatssu.domain.partnership.persistence.PartnershipRepository;
import ssu.eatssu.domain.partnership.persistence.PartnershipRestaurantRepository;
import ssu.eatssu.domain.user.department.persistence.CollegeRepository;
import ssu.eatssu.domain.user.department.persistence.DepartmentRepository;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartnershipServiceTest {

    @Mock private PartnershipRepository partnershipRepository;
    @Mock private CollegeRepository collegeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private PartnershipLikeRepository partnershipLikeRepository;
    @Mock private PartnershipRestaurantRepository partnershipRestaurantRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private PartnershipService partnershipService;

    @Test
    void createPartnershipThrowsWhenRestaurantDoesNotExist() {
        CreatePartnershipRequest request = request();
        given(partnershipRestaurantRepository.findById(request.getStoreId())).willReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.createPartnership(request))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void togglePartnershipLikeThrowsWhenPartnershipDoesNotExist() {
        given(partnershipRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> partnershipService.togglePartnershipLike(1L, userDetails()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void togglePartnershipLikeSavesNewLike() {
        PartnershipRestaurant restaurant = org.mockito.Mockito.mock(PartnershipRestaurant.class);
        given(restaurant.getLikes()).willReturn(new ArrayList<>());
        Partnership partnership = org.mockito.Mockito.mock(Partnership.class);
        User user = org.mockito.Mockito.mock(User.class);
        given(partnershipRepository.findById(1L)).willReturn(Optional.of(partnership));
        given(partnership.getPartnershipRestaurant()).willReturn(restaurant);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(partnershipLikeRepository.findByUserAndPartnershipRestaurant(user, restaurant)).willReturn(Optional.empty());

        partnershipService.togglePartnershipLike(1L, userDetails());

        verify(partnershipLikeRepository).save(any(PartnershipLike.class));
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void togglePartnershipLikeDeletesExistingLike() {
        PartnershipRestaurant restaurant = org.mockito.Mockito.mock(PartnershipRestaurant.class);
        PartnershipLike like = org.mockito.Mockito.mock(PartnershipLike.class);
        ArrayList<PartnershipLike> likes = new ArrayList<>();
        likes.add(like);
        given(restaurant.getLikes()).willReturn(likes);
        Partnership partnership = org.mockito.Mockito.mock(Partnership.class);
        User user = org.mockito.Mockito.mock(User.class);
        given(partnershipRepository.findById(1L)).willReturn(Optional.of(partnership));
        given(partnership.getPartnershipRestaurant()).willReturn(restaurant);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(partnershipLikeRepository.findByUserAndPartnershipRestaurant(user, restaurant)).willReturn(Optional.of(like));

        partnershipService.togglePartnershipLike(1L, userDetails());

        verify(partnershipLikeRepository).delete(like);
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void getUserDepartmentPartnershipsThrowsWhenDepartmentIsMissing() {
        User user = org.mockito.Mockito.mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        assertThatThrownBy(() -> partnershipService.getUserDepartmentPartnerships(userDetails()))
                .isInstanceOf(BaseException.class);
    }

    private CreatePartnershipRequest request() {
        return new CreatePartnershipRequest(1L, "IT대", "컴퓨터학부", "10% 할인", LocalDate.now(), LocalDate.now());
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
