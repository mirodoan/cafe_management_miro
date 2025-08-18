package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.reports.ReportFilterRequest;

import java.util.List;

/**
 * ReportService
 * Interface định nghĩa các chức năng dịch vụ liên quan đến báo cáo trong hệ thống quán cafe.
 * - Cho phép lấy dữ liệu báo cáo theo bộ lọc.
 * - Hỗ trợ sinh file báo cáo (PDF, Excel, ...) theo điều kiện lọc.
 */
public interface ReportService {
    /**
     * Tạo báo cáo (file, ví dụ PDF, Excel) dựa trên bộ lọc.
     *
     * @param request Bộ lọc báo cáo.
     * @return Mảng byte của file báo cáo được sinh ra.
     */
    byte[] generateReport(ReportFilterRequest request);

    byte[] generateReportExcel(ReportFilterRequest request); // Excel

    /**
     * Lấy dữ liệu báo cáo theo bộ lọc.
     *
     * @param request Bộ lọc báo cáo.
     * @return Danh sách dữ liệu báo cáo.
     */
    List<?> getReportData(ReportFilterRequest request);
}