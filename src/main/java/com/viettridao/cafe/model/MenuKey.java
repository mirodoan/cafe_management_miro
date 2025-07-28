package com.viettridao.cafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
/**
 * Composite key cho MenuDetailEntity (product_id, menu_item_id)
 */
public class MenuKey {
    @Column(name = "product_id")
    private Integer idProduct; // ID của sản phẩm liên kết với chi tiết menu

    @Column(name = "menu_item_id")
    private Integer idMenuItem; // ID của món ăn liên kết với chi tiết menu

    @Override
    public boolean equals(Object obj) {
        // Kiểm tra nếu hai đối tượng là cùng một vùng nhớ (cùng tham chiếu)
        if (this == obj)
            return true;
        // Kiểm tra nếu obj null hoặc không cùng class (không cùng kiểu đối tượng)
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Ép kiểu obj về MenuKey để so sánh các trường
        MenuKey that = (MenuKey) obj;
        // So sánh idProduct và idMenuItem của hai đối tượng
        return java.util.Objects.equals(idProduct, that.idProduct) &&
                java.util.Objects.equals(idMenuItem, that.idMenuItem);
    }

    @Override
    public int hashCode() {
        // Tính hash code dựa trên hai trường idProduct và idMenuItem
        return java.util.Objects.hash(idProduct, idMenuItem);
    }
}
