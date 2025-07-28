package com.viettridao.cafe.dto.request.sales;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateSelectMenuRequest {
    @NotNull(message = "ID bàn không được để trống")
    @Min(value = 1, message = "ID bàn phải lớn hơn 0")
    private Integer tableId; // ID bàn được chọn

    private String customerName; // Tên khách hàng

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
