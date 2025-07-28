package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Thực thể Unit lưu thông tin đơn vị tính.
 */
@Getter
@Setter
@Entity
@Table(name = "units") // Bảng lưu thông tin đơn vị tính
public class UnitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "unit_name") // Tên đơn vị tính
    private String unitName;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<ProductEntity> products; // Danh sách sản phẩm liên kết với đơn vị tính
}