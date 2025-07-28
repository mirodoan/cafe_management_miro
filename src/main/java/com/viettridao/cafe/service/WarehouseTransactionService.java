package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionPageResponse;

/**
 * WarehouseTransactionService
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
public interface WarehouseTransactionService {
    /**
     * Lấy danh sách giao dịch kho theo từ khóa tìm kiếm và phân trang.
     *
     * @param keyword Từ khóa tìm kiếm.
     * @param page    Số trang cần lấy.
     * @param size    Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng WarehouseTransactionPageResponse chứa danh sách giao dịch và thông tin phân trang.
     */
    WarehouseTransactionPageResponse getTransactions(String keyword, int page, int size);
}