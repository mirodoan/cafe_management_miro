package com.viettridao.cafe.dto.request.equipment;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateEquipmentRequest {
    @NotNull(message = "Id thiết bị không được để trống")
    @Min(value = 1, message = "Id thiết bị phải lớn hơn 0")
    private Integer id;

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(min = 5, message = "Tên thiết bị tối thiểu 5 ký tự")
    private String equipmentName;

    @NotNull(message = "Số lượng thiết bị không được để trống")
    @Min(value = 1, message = "Số lượng thiết bị phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Ngày mua không được để trống")
    @PastOrPresent(message = "Ngày mua không được lớn hơn ngày hiện tại")
    private LocalDate purchaseDate;

    @NotNull(message = "Giá mua không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá mua phải lớn hơn 0")
    private Double purchasePrice;
}