package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "menu_items") // thucdon
public class MenuItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_item_id")
    private Integer id; // Khóa chính của bảng menu_items

    @Column(name = "item_name")
    private String itemName; // Tên món ăn trong thực đơn

    @Column(name = "current_price")
    private Double currentPrice; // Giá hiện tại của món ăn

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của món ăn

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private List<MenuDetailEntity> menuDetails; // Danh sách chi tiết menu liên kết với món ăn

    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private List<InvoiceDetailEntity> invoiceDetails; // Danh sách chi tiết hóa đơn liên kết với món ăn
}
