package com.viettridao.cafe.dto.request.menu_item;

import com.viettridao.cafe.dto.request.menu_item_detail.CreateMenuItemDetailRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateMenuItemRequest {
    @NotBlank(message = "Tên thực đơn không được để trống")
    @Size(min = 3, message = "Tên thực đơn tối thiểu 3 ký tự")
    @Size(max = 20, message = "Tên thực đơn tối đa 20 ký tự")
    private String itemName;

    @NotNull(message = "Vui lòng nhập giá tiền")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá tiền phải là số lớn hơn 0")
    @DecimalMax(value = "999999999.0", message = "Giá tiền phải là số nhỏ hơn hoặc bằng 999,999,999")
    private Double currentPrice;

    @NotNull(message = "Chi tiết thực đơn không được để trống")
    private List<CreateMenuItemDetailRequest> menuDetails;
}