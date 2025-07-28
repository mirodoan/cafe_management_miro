package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.export.CreateExportRequest;
import com.viettridao.cafe.dto.request.export.UpdateExportRequest;

/**
 * ExportService
 *
 * Version 1.0
 *
 * Date: 18-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 */
public interface ExportService {

    /**
     * Tạo mới một đơn xuất.
     *
     * @param request Thông tin đơn xuất cần tạo.
     */
    void createExport(CreateExportRequest request);

    /**
     * Lấy thông tin đơn xuất để cập nhật.
     *
     * @param id ID của đơn xuất cần cập nhật.
     * @return Thông tin cập nhật đơn xuất.
     */
    UpdateExportRequest getUpdateForm(Integer id);

    /**
     * Cập nhật thông tin đơn xuất.
     *
     * @param id      ID của đơn xuất cần cập nhật.
     * @param request Thông tin cập nhật đơn xuất.
     */
    void updateExport(Integer id, UpdateExportRequest request);

    /**
     * Xóa đơn xuất dựa trên ID.
     *
     * @param id ID của đơn xuất cần xóa.
     */
    void deleteExport(Integer id);
}