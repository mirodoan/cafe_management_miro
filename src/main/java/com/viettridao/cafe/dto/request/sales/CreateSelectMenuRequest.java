package com.viettridao.cafe.dto.request.sales;

import com.viettridao.cafe.exception.OptionalSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreateSelectMenuRequest {
    @NotNull(message = "ID bàn không được để trống")
    @Min(value = 1, message = "ID bàn phải lớn hơn 0")
    private Integer tableId; // ID bàn được chọn

    @OptionalSize(min = 3, max = 20, message = "Tên phải từ 3 đến 20 ký tự nếu được nhập")
    private String customerName; // Tên khách hàng

    @OptionalSize(min = 10, max = 11, message = "Số điện thoại phải bắt đầu từ 0 và có từ 10 đến 11 chữ số nếu được nhập")
    private String customerPhone; // Số điện thoại khách hàng

    @NotNull(message = "Danh sách món không được để trống")
    @Size(min = 1, message = "Phải chọn ít nhất 1 món")
    private List<MenuOrderItem> items = new ArrayList<>(); // List các món chọn

    @Getter
    @Setter
    public static class MenuOrderItem {
        @NotNull(message = "ID món không được để trống")
        @Min(value = 1, message = "ID món phải lớn hơn 0")
        private Integer menuItemId; // ID món trong menu

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải lớn hơn 0")
        @Max(value = 999, message = "Số lượng phải nhỏ hơn 999")
        private Integer quantity; // Số lượng món được chọn
    }
}
