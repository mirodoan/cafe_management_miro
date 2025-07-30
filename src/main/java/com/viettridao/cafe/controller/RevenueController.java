package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.reservation.RevenueFilterRequest;
import com.viettridao.cafe.dto.response.revenue.RevenueResponse;
import com.viettridao.cafe.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * RevenueController
 * Controller quản lý các chức năng hiển thị báo cáo doanh thu cho hệ thống quán cafe.
 * - Hiển thị trang báo cáo doanh thu theo khoảng thời gian lọc bởi người dùng.
 * - Gọi service để truy xuất dữ liệu doanh thu và truyền sang view.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/revenue")
public class RevenueController {
    private final RevenueService revenueService;

    /**
     * Hiển thị báo cáo doanh thu theo khoảng thời gian.
     */
    @GetMapping
    public String viewRevenue(
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            Model model
    ) {
        // Nếu chưa nhập thì lấy toàn bộ dữ liệu
        if (startDate == null) {
            startDate = LocalDate.of(2000, 1, 1); // hoặc ngày sớm nhất bạn muốn hiển thị
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        RevenueFilterRequest filter = new RevenueFilterRequest();
        filter.setStartDate(startDate);
        filter.setEndDate(endDate);

        // Khởi tạo dữ liệu mặc định nếu không có kết quả
        RevenueResponse revenue = new RevenueResponse();
        revenue.setSummaries(new ArrayList<>());
        revenue.setTotalIncome(0.0);
        revenue.setTotalExpense(0.0);

        try {
            revenue = revenueService.getRevenueSummary(filter);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        model.addAttribute("filter", filter);
        model.addAttribute("revenue", revenue);
        return "/revenues/revenue";
    }

}