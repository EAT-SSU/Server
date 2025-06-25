package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ssu.eatssu.domain.admin.dto.InquiryLine;
import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.dto.UpdateStatusRequest;
import ssu.eatssu.domain.admin.service.ManageInquiryService;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/inquiries")
@RequiredArgsConstructor
public class ManageInquiryController {
    private final ManageInquiryService manageInquiryService;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<PageWrapper<InquiryLine>> inquiryPage(@PageableDefault(size = 20) Pageable pageable,
                                                              Model model) {
        PageWrapper<InquiryLine> inquiryLinePage = manageInquiryService.getInquiryBoard(pageable);
        return BaseResponse.success(inquiryLinePage);
    }

    @ResponseBody
    @PatchMapping("{inquiryId}/status")
    public BaseResponse<?> updateStatus(@PathVariable Long inquiryId, @RequestBody UpdateStatusRequest request) {
        manageInquiryService.updateStatus(inquiryId, request);
        return BaseResponse.success();
    }

    @ResponseBody
    @DeleteMapping("{inquiryId}")
    public BaseResponse<?> delete(@PathVariable Long inquiryId) {
        manageInquiryService.delete(inquiryId);
        return BaseResponse.success();
    }
}
