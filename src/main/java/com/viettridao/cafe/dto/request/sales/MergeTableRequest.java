package com.viettridao.cafe.dto.request.sales;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MergeTableRequest {
    @NotNull(message = "Danh sách bàn cần gộp không được để trống")
    @Size(min = 2, message = "Phải chọn ít nhất 2 bàn để gộp")
    private List<@NotNull(message = "ID bàn không được để trống") @Min(value = 1, message = "ID bàn phải lớn hơn 0") Integer> tableIds;

    @NotNull(message = "Phải chọn bàn gộp đến")
    @Min(value = 1, message = "ID bàn gộp đến phải lớn hơn 0")
    private Integer targetTableId;
}
