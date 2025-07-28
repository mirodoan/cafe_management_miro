package com.viettridao.cafe.dto.request.sales;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SplitTableRequest {
    @NotNull(message = "ID bàn nguồn không được để trống")
    @Min(value = 1, message = "ID bàn nguồn phải lớn hơn 0")
    private Integer sourceTableId;

    @NotNull(message = "ID bàn đích không được để trống")
    @Min(value = 1, message = "ID bàn đích phải lớn hơn 0")
    private Integer targetTableId;

    @NotNull(message = "Danh sách món cần tách không được để trống")
    @Size(min = 1, message = "Phải chọn ít nhất 1 món để tách")
    @Valid
    private List<SplitItemRequest> items;

    @Getter
    @Setter
    public static class SplitItemRequest {
        @NotNull(message = "ID món không được để trống")
        @Min(value = 1, message = "ID món phải lớn hơn 0")
        private Integer menuItemId;

        @NotNull(message = "Số lượng tách không được để trống")
        @Min(value = 0, message = "Số lượng tách không được âm")
        private Integer quantity;
    }
}
