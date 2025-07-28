package com.viettridao.cafe.dto.request.sales;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveTableRequest {
    @NotNull(message = "Bàn nguồn không được để trống")
    @Min(value = 1, message = "ID bàn nguồn phải lớn hơn 0")
    private Integer sourceTableId;

    @NotNull(message = "Bàn đích không được để trống")
    @Min(value = 1, message = "ID bàn đích phải lớn hơn 0")
    private Integer targetTableId;
}
