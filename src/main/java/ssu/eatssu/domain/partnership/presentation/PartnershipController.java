package ssu.eatssu.domain.partnership.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.request.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.response.PartnershipResponse;
import ssu.eatssu.domain.partnership.presentation.docs.PartnershipControllerDocs;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/partnerships")
@RequiredArgsConstructor
public class PartnershipController implements PartnershipControllerDocs {
    private final PartnershipService partnershipService;

    @Override
    @PostMapping
    public BaseResponse<?> createPartnership(@RequestBody CreatePartnershipRequest request) {
        partnershipService.createPartnership(request);
        return BaseResponse.success();
    }

    @Override
    @GetMapping
    public BaseResponse<List<PartnershipResponse>> getAllPartnerships(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getAllPartnerships(userDetails));
    }


    @Override
    @PostMapping("/{partnershipId}/like")
    public BaseResponse<?> togglePartnershipLike(
            @PathVariable Long partnershipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        partnershipService.togglePartnershipLike(partnershipId, userDetails);
        return BaseResponse.success();
    }
}
