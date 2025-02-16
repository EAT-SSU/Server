package ssu.eatssu.domain.partnership.presentation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.entity.Partnership;
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
    public ResponseEntity<List<Partnership>> getAllPartnerships() {
        return ResponseEntity.ok(partnershipService.getAllPartnerships());
    }
}
