package com.viettridao.cafe.dto.request.imports;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO cho việc tạo mới thông tin đơn nhập hàng.
 * Bao gồm các trường như ngày nhập, số lượng và thông tin sản phẩm.
 */
@Getter
@Setter
public class CreateImportRequest {
    @NotNull(message = "Ngày nhập không được để trống")
    @PastOrPresent(message = "Ngày nhập không được vượt quá ngày hôm nay")
    private LocalDate importDate;

    @NotNull(message = "Đơn giá nhập không được để trống")
    @Positive(message = "Đơn giá nhập phải lớn hơn 0")
    private Double unitImportPrice;

    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Id hàng hóa không được để trống")
    @Min(value = 1, message = "Id hàng hóa phải lớn hơn 0")
    private Integer productId;

    @Min(value = 1, message = "Id nhân viên phải lớn hơn 0")
    private Integer employeeId;
}
