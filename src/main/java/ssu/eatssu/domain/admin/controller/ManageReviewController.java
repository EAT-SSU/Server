package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ssu.eatssu.domain.admin.service.ManageReviewService;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class ManageReviewController {
    private final ManageReviewService manageReviewService;

    @ResponseBody
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> delete(@PathVariable Long reviewId) {
        manageReviewService.delete(reviewId);
        return BaseResponse.success();
    }
}
