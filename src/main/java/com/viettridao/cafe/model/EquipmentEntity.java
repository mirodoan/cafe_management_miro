package com.viettridao.cafe.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Thực thể Equipment lưu thông tin thiết bị.
 */
@Getter
@Setter
@Entity
@Table(name = "equipment") // Bảng lưu thông tin thiết bị
public class EquipmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "equipment_name") // Tên thiết bị
    private String equipmentName;

    @Column(name = "quantity") // Số lượng thiết bị
    private Integer quantity;

    @Column(name = "notes") // Ghi chú về thiết bị
    private String notes;

    @Column(name = "purchase_date") // Ngày mua thiết bị
    private LocalDate purchaseDate;

    @Column(name = "purchase_price") // Giá mua thiết bị
    private Double purchasePrice;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;
}