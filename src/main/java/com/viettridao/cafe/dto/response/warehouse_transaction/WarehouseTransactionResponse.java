package com.viettridao.cafe.dto.response.warehouse_transaction;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến giao dịch kho hàng.
 * Lớp này bao gồm chi tiết về imports, exports và thông tin sản phẩm.
 */
@Getter
@Setter
public class WarehouseTransactionResponse {
    private Integer id;

    private String productName;

    private String unitName;

    private Integer quantity;

    private Double unitPrice;

    private Double totalAmount;

    private LocalDate importDate;

    private LocalDate exportDate;

    private String type;
}