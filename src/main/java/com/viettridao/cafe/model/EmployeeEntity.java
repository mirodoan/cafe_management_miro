package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Thực thể Employee lưu thông tin nhân viên.
 */
@Entity
@Getter
@Setter
@Table(name = "employees") // Bảng lưu thông tin nhân viên
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "full_name") // Tên đầy đủ của nhân viên
    private String fullName;

    @Column(name = "phone_number") // Số điện thoại của nhân viên
    private String phoneNumber;

    @Column(name = "address") // Địa chỉ của nhân viên
    private String address;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id") // Tài khoản liên kết với nhân viên
    private AccountEntity account;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "position_id") // Chức vụ của nhân viên
    private PositionEntity position;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ImportEntity> imports; // Danh sách đơn nhập liên kết với nhân viên

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ExportEntity> exports; // Danh sách đơn xuất liên kết với nhân viên

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations; // Danh sách đặt chỗ liên kết với nhân viên
}
