package com.viettridao.cafe.dto.request.promotion;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO cho việc tạo mới thông tin khuyến mãi.
 * Bao gồm các trường như tên khuyến mãi, ngày bắt đầu, ngày kết thúc và giá trị
 * giảm giá.
 */
@Getter
@Setter
public class CreatePromotionRequest {
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(min = 5, message = "Tên khuyến mãi tối thiểu 5 ký tự")
    private String promotionName;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là hôm nay hoặc trong tương lai")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDate endDate;

    @AssertTrue(message = "Ngày kết thúc phải lớn hơn ngày bắt đầu")
    public boolean isEndDateAfterStartDate() {
        if (startDate == null || endDate == null) return true;
        return endDate.isAfter(startDate);
    }

    @NotNull(message = "Phần trăm giảm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Phần trăm giảm phải lớn hơn 0")
    private Double discountValue;
}
