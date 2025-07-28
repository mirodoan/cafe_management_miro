package com.viettridao.cafe.dto.response.equipment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến thiết bị.
 * Bao gồm thông tin về tên thiết bị, số lượng, ghi chú, ngày mua và giá mua.
 */
@Getter
@Setter
public class EquipmentResponse {
    private Integer id;

    private String equipmentName;

    private Integer quantity;

    private String notes;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    private Double purchasePrice;

    private Boolean isDeleted;

}