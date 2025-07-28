package com.viettridao.cafe.model;

import com.viettridao.cafe.common.InvoiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "invoices") // hoadon
public class InvoiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer id;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status; // Trạng thái của hóa đơn (ví dụ: đã thanh toán, hủy ...)

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của hóa đơn

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "promotion_id")
    private PromotionEntity promotion; // Khuyến mãi áp dụng cho hóa đơn

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceDetailEntity> invoiceDetails; // Danh sách chi tiết hóa đơn liên kết với hóa đơn

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations; // Danh sách đặt chỗ liên kết với hóa đơn
}
