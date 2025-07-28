package com.viettridao.cafe.dto.response.export;

import java.time.LocalDate;

import com.viettridao.cafe.dto.response.employee.EmployeeResponse;
import com.viettridao.cafe.dto.response.product.ProductResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến xuất hàng.
 */
@Getter
@Setter
public class ExportResponse {
    private Integer id;

    private Double unitExportPrice;

    private LocalDate exportDate;

    private Integer quantity;

    private Boolean deleted;

    private EmployeeResponse employee;

    private ProductResponse product;

}
