package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Thực thể InvoiceDetail lưu thông tin chi tiết hóa đơn.
 */
@Getter
@Setter
@Entity
@Table(name = "invoice_details") // Bảng lưu thông tin chi tiết hóa đơn
public class InvoiceDetailEntity {
    @EmbeddedId
    private InvoiceKey id; // Khóa chính của bảng, bao gồm id hóa đơn và id món ăn

    @ManyToOne
    @MapsId("idInvoice")
    @JoinColumn(name = "invoice_id") // Hóa đơn liên kết với chi tiết hóa đơn
    private InvoiceEntity invoice;

    @ManyToOne
    @MapsId("idMenuItem")
    @JoinColumn(name = "menu_item_id") // Món ăn liên kết với chi tiết hóa đơn
    private MenuItemEntity menuItem;

    @Column(name = "quantity") // Số lượng món ăn
    private Integer quantity;

    @Column(name = "price_at_sale_time") // Giá tại thời điểm bán
    private Double price;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;
}
