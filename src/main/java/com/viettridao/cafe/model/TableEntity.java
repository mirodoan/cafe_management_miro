package com.viettridao.cafe.model;

import com.viettridao.cafe.common.TableStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tables") // ban
public class TableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "table_id")
    private Integer id; // Khóa chính của bảng tables

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TableStatus status; // Trạng thái của bàn (ví dụ: trống, đã đặt, đang sử dụng)

    @Column(name = "table_name")
    private String tableName; // Tên hoặc số hiệu của bàn

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete) của bàn

    @OneToMany(mappedBy = "table", cascade = CascadeType.ALL)
    private List<ReservationEntity> reservations; // Danh sách các đặt chỗ liên kết với bàn
}
