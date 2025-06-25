package ssu.eatssu.domain.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ssu.eatssu.domain.admin.dto.PageWrapper;
import ssu.eatssu.domain.admin.dto.ReportLine;
import ssu.eatssu.domain.admin.service.ManageReportService;
import ssu.eatssu.global.handler.response.BaseResponse;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ManageReportController {
    private final ManageReportService manageReportService;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<PageWrapper<ReportLine>> reportPage(@PageableDefault(size = 20) Pageable pageable,
                                                            Model model) {
        PageWrapper<ReportLine> reportLinePage = manageReportService.getReportBoard(pageable);
        return BaseResponse.success(reportLinePage);
    }

    @ResponseBody
    @DeleteMapping("/{reportId}")
    public BaseResponse<?> delete(@PathVariable Long reportId) {
        manageReportService.delete(reportId);
        return BaseResponse.success();
    }

}
