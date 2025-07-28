package com.viettridao.cafe.dto.request.menu_item_detail;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMenuItemDetailRequest {
    private Integer id; // Để null khi tạo mới, có giá trị khi update

    @NotNull(message = "Id hàng hóa chi tiết thực đơn không được để trống")
    @Min(value = 1, message = "Id hàng hóa chi tiết thực đơn phải lớn hơn 0")
    private Integer productId;

    @NotNull(message = "Khối lượng thành phần không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Khối lượng thành phần phải lớn hơn 0")
    private Double quantity;

    @NotNull(message = "Đơn vị tính không được để trống")
    @Min(value = 1, message = "Id đơn vị tính phải lớn hơn 0")
    private Integer unitId;
}
