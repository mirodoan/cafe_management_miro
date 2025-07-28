package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Thực thể Export lưu thông tin đơn xuất hàng.
 */
@Getter
@Setter
@Entity
@Table(name = "exports") // Bảng lưu thông tin đơn xuất hàng
public class ExportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exports_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "unit_export_price")
    private Double unitExportPrice; // Đơn giá sản phẩm khi xuất

    @Column(name = "total_export_amount") // Tổng số tiền xuất hàng
    private Double totalAmount;

    @Column(name = "export_date") // Ngày xuất hàng
    private LocalDate exportDate;

    @Column(name = "quantity") // Số lượng hàng xuất
    private Integer quantity;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id") // Nhân viên thực hiện đơn xuất
    private EmployeeEntity employee;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id") // Sản phẩm được xuất
    private ProductEntity product;
}
