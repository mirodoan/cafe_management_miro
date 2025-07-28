package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Thực thể Expense lưu thông tin chi tiêu.
 */
@Getter
@Setter
@Entity
@Table(name = "expenses") // Bảng lưu thông tin chi tiêu
public class ExpenseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "amount") // Số tiền chi tiêu
    private Double amount;

    @Column(name = "expense_name") // Tên chi tiêu
    private String expenseName;

    @Column(name = "expense_date") // Ngày chi tiêu
    private LocalDate expenseDate;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "account_id") // Tài khoản liên kết với chi tiêu
    private AccountEntity account;
}
