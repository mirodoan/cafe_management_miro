package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.reservation.RevenueFilterRequest;
import com.viettridao.cafe.dto.response.revenue.RevenueResponse;

/**
 * RevenueService
 * Interface định nghĩa các chức năng dịch vụ liên quan đến tổng hợp doanh thu cho hệ thống quán cafe.
 * - Cho phép lấy tổng hợp doanh thu dựa trên các điều kiện lọc từ phía người dùng.
 */
public interface RevenueService {
    /**
     * Lấy tổng hợp doanh thu theo bộ lọc.
     *
     * @param request Bộ lọc thống kê doanh thu.
     * @return Đối tượng RevenueResponse chứa thông tin tổng hợp doanh thu.
     */
    RevenueResponse getRevenueSummary(RevenueFilterRequest request);
}