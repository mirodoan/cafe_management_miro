package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.employee.CreateEmployeeRequest;
import com.viettridao.cafe.dto.request.employee.UpdateEmployeeRequest;
import com.viettridao.cafe.dto.response.employee.EmployeePageResponse;
import com.viettridao.cafe.model.EmployeeEntity;

/**
 * EmployeeService
 *
 * Version 1.0
 *
 * Date: 18-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 */
public interface EmployeeService {

    /**
     * Lấy danh sách tất cả nhân viên theo từ khóa tìm kiếm và phân trang.
     *
     * @param keyword Từ khóa tìm kiếm theo tên nhân viên.
     * @param page    Số trang cần lấy.
     * @param size    Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng EmployeePageResponse chứa danh sách nhân viên và thông tin phân trang.
     */
    EmployeePageResponse getAllEmployees(String keyword, int page, int size);

    /**
     * Kiểm tra username đã tồn tại trong hệ thống chưa (dành cho tạo mới nhân viên).
     * @param username tên đăng nhập
     * @return true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByUsername(String username);

    /**
     * Tạo mới một nhân viên.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo nhân viên mới.
     * @return Thực thể EmployeeEntity vừa được tạo.
     */
    EmployeeEntity createEmployee(CreateEmployeeRequest request);

    /**
     * Xóa một nhân viên dựa trên ID.
     *
     * @param id ID của nhân viên cần xóa.
     */
    void deleteEmployee(Integer id);

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho nhân viên.
     */
    void updateEmployee(UpdateEmployeeRequest request);

    /**
     * Lấy thông tin chi tiết của một nhân viên dựa trên ID.
     *
     * @param id ID của nhân viên cần lấy thông tin.
     * @return Thực thể EmployeeEntity tương ứng với ID.
     */
    EmployeeEntity getEmployeeById(Integer id);
}