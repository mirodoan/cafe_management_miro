package com.viettridao.cafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Composite key cho MenuDetailEntity (product_id, menu_item_id, unit_id)
 */
@Getter
@Setter
@Embeddable
public class MenuKey {
    @Column(name = "product_id")
    private Integer idProduct; // ID của sản phẩm liên kết với chi tiết menu

    @Column(name = "menu_item_id")
    private Integer idMenuItem; // ID của món ăn liên kết với chi tiết menu

    @Column(name = "unit_id")
    private Integer idUnit; // ID của đơn vị liên kết với chi tiết menu

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        MenuKey that = (MenuKey) obj;
        // So sánh cả 3 trường để phân biệt từng bản ghi
        return java.util.Objects.equals(idProduct, that.idProduct) &&
                java.util.Objects.equals(idMenuItem, that.idMenuItem) &&
                java.util.Objects.equals(idUnit, that.idUnit);
    }

    @Override
    public int hashCode() {
        // Tính hash code dựa trên ba trường
        return java.util.Objects.hash(idProduct, idMenuItem, idUnit);
    }
}