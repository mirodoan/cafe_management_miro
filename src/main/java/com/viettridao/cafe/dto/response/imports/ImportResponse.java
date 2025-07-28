package com.viettridao.cafe.dto.response.imports;

import java.time.LocalDate;

import com.viettridao.cafe.dto.response.employee.EmployeeResponse;
import com.viettridao.cafe.dto.response.product.ProductResponse;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến nhập hàng.
 * Bao gồm thông tin về ngày nhập, sản phẩm, giá, đơn vị và số lượng.
 */
@Getter
@Setter
public class ImportResponse {
    private Integer id;

    private LocalDate importDate;

    private Double unitImportPrice;

    private Integer quantity;

    private Boolean deleted;

    private EmployeeResponse employee;

    private ProductResponse product;
}
