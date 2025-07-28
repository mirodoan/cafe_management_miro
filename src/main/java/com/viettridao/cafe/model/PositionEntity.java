package com.viettridao.cafe.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Thực thể Position lưu thông tin chức vụ.
 */
@Getter
@Setter
@Entity
@Table(name = "positions") // Bảng lưu thông tin chức vụ
public class PositionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id") // Khóa chính của bảng
    private Integer id;

    @Column(name = "salary") // Mức lương của chức vụ
    private Double salary;

    @Column(name = "position_name") // Tên chức vụ
    private String positionName;

    @Column(name = "is_deleted") // Trạng thái xóa mềm (soft delete)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "position")
    private List<EmployeeEntity> employees; // Danh sách nhân viên liên kết với chức vụ
}
