package com.viettridao.cafe.dto.request.sales;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateSelectMenuRequest {
    @NotNull(message = "ID bàn không được để trống")
    @Min(value = 1, message = "ID bàn phải lớn hơn 0")
    private Integer tableId; // ID bàn được chọn

    @NotBlank(message = "Tên khách hàng không được để trống")
    @Size(min = 3, message = "Tên khách hàng tối thiểu 3 ký tự")
    private String customerName; // Tên khách hàng

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại phải bắt đầu từ 0 và có từ 10 đến 11 chữ số")
    private String customerPhone; // Số điện thoại khách hàng

    @NotNull(message = "Danh sách món không được để trống")
    @Size(min = 1, message = "Phải chọn ít nhất 1 món")
    private List<MenuOrderItem> items; // List các món chọn

    @Getter
    @Setter
    public static class MenuOrderItem {
        @NotNull(message = "ID món không được để trống")
        @Min(value = 1, message = "ID món phải lớn hơn 0")
        private Integer menuItemId; // ID món trong menu

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        private Integer quantity; // Số lượng món được chọn
    }
}
