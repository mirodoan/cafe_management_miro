package com.viettridao.cafe.dto.request.menu_item;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import com.viettridao.cafe.dto.request.menu_item_detail.CreateMenuItemDetailRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMenuItemRequest {
    @NotBlank(message = "Tên thực đơn không được để trống")
    @Size(min = 3, message = "Tên thực đơn tối thiểu 3 ký tự")
    private String itemName;

    @NotNull(message = "Vui lòng nhập giá tiền")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá tiền phải là số lớn hơn 0. Ví dụ: 50000 hoặc 50000.5")
    private Double currentPrice;

    @NotNull(message = "Chi tiết thực đơn không được để trống")
    private List<CreateMenuItemDetailRequest> menuDetails;
}