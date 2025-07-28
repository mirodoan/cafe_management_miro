package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.imports.CreateImportRequest;
import com.viettridao.cafe.dto.request.imports.UpdateImportRequest;

/**
 * ImportService
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
public interface ImportService {

    /**
     * Tạo mới một đơn nhập.
     *
     * @param request Thông tin đơn nhập cần tạo.
     */
    void createImport(CreateImportRequest request);

    /**
     * Lấy thông tin đơn nhập để cập nhật.
     *
     * @param id ID của đơn nhập cần cập nhật.
     * @return Thông tin cập nhật đơn nhập.
     */
    UpdateImportRequest getUpdateForm(Integer id);

    /**
     * Cập nhật thông tin đơn nhập.
     *
     * @param id      ID của đơn nhập cần cập nhật.
     * @param request Thông tin cập nhật đơn nhập.
     */
    void updateImport(Integer id, UpdateImportRequest request);

    /**
     * Xóa đơn nhập dựa trên ID.
     *
     * @param id ID của đơn nhập cần xóa.
     */
    void deleteImport(Integer id);
}