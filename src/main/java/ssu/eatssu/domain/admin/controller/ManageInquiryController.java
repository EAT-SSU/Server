package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ssu.eatssu.domain.admin.dto.InquiryLine;
import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.service.ManageInquiryService;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/inquiry")
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
}
