package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionPageResponse;

/**
 * WarehouseTransactionService
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