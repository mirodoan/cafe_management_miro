package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Thực thể Product lưu thông tin sản phẩm.
 */
@Getter
@Setter
@Entity
@Table(name = "products") // Bảng lưu thông tin sản phẩm
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer id; // Khóa chính của bảng products

    @Column(name = "product_name")
    private String productName; // Tên sản phẩm

    @Column(name = "quantity")
    private Integer quantity; // Số lượng sản phẩm hiện có trong kho

    @Column(name = "product_price")
    private Double productPrice; // Giá bán của sản phẩm

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của sản phẩm

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ImportEntity> imports; // Danh sách các đơn nhập hàng liên kết với sản phẩm

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ExportEntity> exports; // Danh sách các đơn xuất hàng liên kết với sản phẩm

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<MenuDetailEntity> menuDetails; // Danh sách các chi tiết menu liên kết với sản phẩm

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "unit_id")
    private UnitEntity unit; // Đơn vị tính của sản phẩm, ví dụ: kg, lít, hộp
}
