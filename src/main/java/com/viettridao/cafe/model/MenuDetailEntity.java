package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "menu_item_ingredients")
public class MenuDetailEntity {
    @EmbeddedId
    private MenuKey id; // Khóa chính của bảng, bao gồm ID sản phẩm và ID món ăn

    @ManyToOne
    @MapsId("idProduct")
    @JoinColumn(name = "product_id")
    private ProductEntity product; // Sản phẩm liên kết với chi tiết menu

    @ManyToOne
    @MapsId("idMenuItem")
    @JoinColumn(name = "menu_item_id")
    private MenuItemEntity menuItem; // Món ăn liên kết với chi tiết menu

    @Column(name = "quantity")
    private Double quantity; // Trọng lượng sản phẩm trong món ăn

    @ManyToOne
    @JoinColumn(name = "unit_id", insertable = false, updatable = false)
    private UnitEntity unit;

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của chi tiết menu
}
