package ssu.eatssu.domain.partnership.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.PartnershipDetailResponse;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequestMapping("/partnerships")
@RequiredArgsConstructor
public class PartnershipController {
    private final PartnershipService partnershipService;

    @PostMapping
    public BaseResponse<?> createPartnership(@RequestBody CreatePartnershipRequest request) {
        partnershipService.createPartnership(request);
        return BaseResponse.success();
    }

    @GetMapping
    public BaseResponse<List<PartnershipResponse>> getAllPartnerships() {
        return BaseResponse.success(partnershipService.getAllPartnerships());
    }

    @GetMapping("/{partnershipId}")
    public BaseResponse<PartnershipDetailResponse> getPartnership(
            @PathVariable Long partnershipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getPartnership(partnershipId, userDetails));
    }
}
