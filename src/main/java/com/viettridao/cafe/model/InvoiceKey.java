package com.viettridao.cafe.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

/**
 * Khóa chính cho thực thể InvoiceDetail.
 */
@Getter
@Setter
@Embeddable
/**
 * Composite key cho InvoiceDetailEntity (invoice_id, menu_item_id)
 */
public class InvoiceKey {
    @Column(name = "invoice_id") // ID của hóa đơn
    private Integer idInvoice;

    @Column(name = "menu_item_id") // ID của món ăn
    private Integer idMenuItem;

    @Override
    public boolean equals(Object obj) {
        // Nếu hai tham chiếu trỏ cùng một đối tượng thì chắc chắn bằng nhau
        if (this == obj)
            return true;
        // Nếu obj là null hoặc không phải cùng kiểu class thì không bằng nhau
        if (obj == null || getClass() != obj.getClass())
            return false;
        // Ép kiểu obj về InvoiceKey để so sánh từng trường
        InvoiceKey that = (InvoiceKey) obj;
        // So sánh lần lượt hai trường: idInvoice và idMenuItem
        // Chỉ khi cả hai trường đều bằng nhau thì hai đối tượng mới bằng nhau
        return java.util.Objects.equals(idInvoice, that.idInvoice) &&
                java.util.Objects.equals(idMenuItem, that.idMenuItem);
    }

    @Override
    public int hashCode() {
        // Tạo mã hash dựa trên hai trường idInvoice và idMenuItem
        // Đảm bảo nếu hai đối tượng bằng nhau thì hashCode cũng giống nhau
        return java.util.Objects.hash(idInvoice, idMenuItem);
    }
}
