package com.viettridao.cafe.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "table_reservations_detail") // chitietdatban
public class ReservationEntity {
    @EmbeddedId
    private ReservationKey id; // Khóa chính của bảng, bao gồm ID bàn, ID nhân viên và ID hóa đơn

    @ManyToOne
    @MapsId("idTable")
    @JoinColumn(name = "table_id")
    private TableEntity table; // Bàn liên kết với đặt chỗ

    @ManyToOne
    @MapsId("idEmployee")
    @JoinColumn(name = "employee_id")
    private EmployeeEntity employee; // Nhân viên liên kết với đặt chỗ

    @ManyToOne
    @MapsId("idInvoice")
    @JoinColumn(name = "invoice_id")
    private InvoiceEntity invoice; // Hóa đơn liên kết với đặt chỗ

    @Column(name = "customer_name")
    private String customerName; // Tên khách hàng đặt chỗ

    @Column(name = "customer_phone_number")
    private String customerPhone; // Số điện thoại của khách hàng đặt chỗ

    @Column(name = "reservation_datetime")
    private LocalDateTime reservationDate; // Ngày giờ bắt đầu đặt chỗ

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của đặt chỗ
}
