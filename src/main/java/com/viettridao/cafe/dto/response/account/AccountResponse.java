package com.viettridao.cafe.dto.response.account;

import lombok.Getter;
import lombok.Setter;

/**
 * AccountResponse
 *
 * Version 1.0
 *
 * Date: 19-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 19-07-2025   mirodoan    Create
 */

/**
 * DTO phản hồi thông tin tài khoản nhân viên.
 * Sử dụng để truyền dữ liệu từ backend lên UI/presentation layer.
 * Chỉ chứa các trường cần thiết để hiển thị cho người dùng.
 */
@Getter
@Setter
public class AccountResponse {

    /** Mã định danh tài khoản (primary key) */
    private Integer id;

    /** Họ tên đầy đủ nhân viên */
    private String fullName;

    /** Số điện thoại liên lạc */
    private String phoneNumber;

    /** Địa chỉ thường trú/cư trú */
    private String address;

    /** Đường dẫn ảnh đại diện (avatar) */
    private String imageUrl;

    /** Mã vị trí/chức vụ nhân viên (foreign key) */
    private Integer positionId;

    /** Tên vị trí/chức vụ nhân viên */
    private String positionName;

    /** Lương cơ bản (dữ liệu nhạy cảm, chỉ show khi cần) */
    private Double salary;

    /** Tên đăng nhập hệ thống */
    private String username;

    /** Password đã mã hóa (hash), không trả về giá trị thực */
    private String password;

    /** Trạng thái xóa mềm (soft delete) */
    private Boolean isDeleted;

}