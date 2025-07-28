package com.viettridao.cafe.dto.request.export;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO cho việc tạo mới thông tin đơn xuất hàng.
 * Bao gồm các trường như ngày xuất, số lượng và thông tin sản phẩm.
 */
@Getter
@Setter
public class CreateExportRequest {
    @NotNull(message = "Đơn giá xuất không được để trống")
    @Min(value = 1000, message = "Đơn giá xuất phải lớn hơn hoặc bằng 1000 VNĐ")
    private Double unitExportPrice;

    @NotNull(message = "Ngày xuất không được để trống")
    @PastOrPresent(message = "Ngày xuất không được vượt quá ngày hôm nay")
    private LocalDate exportDate;

    @NotNull(message = "Số lượng xuất không được để trống")
    @Min(value = 1, message = "Số lượng xuất phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Id hàng hóa không được để trống")
    @Min(value = 1, message = "Id hàng hóa phải lớn hơn 0")
    private Integer productId;

    @Min(value = 1, message = "Id nhân viên phải lớn hơn 0")
    private Integer employeeId;
}
