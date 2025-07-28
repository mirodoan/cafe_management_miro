package com.viettridao.cafe.dto.request.export;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc cập nhật thông tin đơn xuất hàng.
 * Bao gồm các trường như ngày xuất, số lượng và thông tin sản phẩm.
 */
@Getter
@Setter
public class UpdateExportRequest {
    @NotNull(message = "Id đơn xuất không được để trống")
    @Min(value = 1, message = "Id đơn xuát phải lớn hơn 0")
    private Integer id;

    @NotNull(message = "Đơn giá xuất không được để trống")
    @Positive(message = "Đơn giá xuất phải lớn hơn 0")
    private Double unitExportPrice;

    @NotNull(message = "Ngày xuất không được để trống")
    @PastOrPresent(message = "Ngày xuất không được lớn hơn ngày hiện tại")
    private LocalDate exportDate;

    @NotNull(message = "Số lượng xuất không được để trống")
    @Min(value = 1, message = "Số xuất nhập phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Id hàng hóa không được để trống")
    @Min(value = 1, message = "Id hàng hóa phải lớn hơn 0")
    private Integer productId;

    @NotNull(message = "Id nhân viên không được để trống")
    @Min(value = 1, message = "Id nhân viên phải lớn hơn 0")
    private Integer employeeId;
}
