package com.viettridao.cafe.controller;

import com.viettridao.cafe.common.ReportType;
import com.viettridao.cafe.dto.request.reports.ReportFilterRequest;
import com.viettridao.cafe.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * ReportController
 * Controller xử lý các chức năng liên quan đến báo cáo (xem, lọc, xuất PDF) cho hệ thống quán cafe.
 * - Hiển thị giao diện báo cáo theo các điều kiện lọc (ngày bắt đầu, ngày kết thúc, loại báo cáo).
 * - Truy vấn dữ liệu báo cáo dựa trên filter từ người dùng.
 * - Hỗ trợ xuất báo cáo ra file PDF để tải về.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    /**
     * Hiển thị form báo cáo và kết quả báo cáo nếu đã filter.
     */
    @GetMapping
    public String showReportForm(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(value = "type", required = false) String type,
            Model model
    ) {
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("type", type);

        // Nếu đã nhập đủ điều kiện lọc, thực hiện truy vấn báo cáo
        if (startDate != null && endDate != null && type != null) {
            try {
                ReportFilterRequest request = new ReportFilterRequest();
                request.setStartDate(startDate);
                request.setEndDate(endDate);
                request.setType(ReportType.valueOf(type));

                List<?> reportData = reportService.getReportData(request);
                model.addAttribute("reportData", reportData);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Loại báo cáo không hợp lệ");
            }
        }

        return "report/report-management";
    }

    /**
     * Export báo cáo ra PDF để download.
     */
    @GetMapping("/download")
    public void downloadPdf(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("type") String type,
            HttpServletResponse response
    ) throws IOException {
        ReportFilterRequest request = new ReportFilterRequest();
        request.setStartDate(startDate);
        request.setEndDate(endDate);
        try {
            request.setType(ReportType.valueOf(type));
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loại báo cáo không hợp lệ");
            return;
        }

        byte[] pdfData = reportService.generateReport(request);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");
        response.getOutputStream().write(pdfData);
        response.getOutputStream().flush();
    }

}