package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.employee.CreateEmployeeRequest;
import com.viettridao.cafe.dto.request.employee.UpdateEmployeeRequest;
import com.viettridao.cafe.dto.response.employee.EmployeePageResponse;
import com.viettridao.cafe.mapper.EmployeeMapper;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.PositionEntity;
import com.viettridao.cafe.repository.AccountRepository;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.service.EmployeeService;
import com.viettridao.cafe.service.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * EmployeeServiceImpl
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
 *
 * Triển khai Service cho thực thể EmployeeEntity.
 * Chịu trách nhiệm xử lý logic nghiệp vụ liên quan đến nhân viên (Employee).
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    // Repository cho thực thể EmployeeEntity
    private final EmployeeRepository employeeRepository;

    // Service cho thực thể PositionEntity
    private final PositionService positionService;

    // Repository cho thực thể AccountEntity
    private final AccountRepository accountRepository;

    // Bộ mã hóa mật khẩu
    private final PasswordEncoder passwordEncoder;

    // Mapper cho thực thể EmployeeEntity
    private final EmployeeMapper employeeMapper;

    /**
     * Lấy danh sách tất cả nhân viên theo từ khóa tìm kiếm và phân trang.
     *
     * @param keyword Từ khóa tìm kiếm theo tên nhân viên.
     * @param page    Số trang cần lấy.
     * @param size    Số lượng bản ghi trên mỗi trang.
     * @return Đối tượng EmployeePageResponse chứa danh sách nhân viên và thông tin phân trang.
     */
    @Override
    public EmployeePageResponse getAllEmployees(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeEntity> employeeEntities;

        if (StringUtils.hasText(keyword)) {
            employeeEntities = employeeRepository.getAllEmployeesBySearch(keyword, pageable);
        } else {
            employeeEntities = employeeRepository.getAllEmployees(pageable);
        }
        EmployeePageResponse employeePageResponse = new EmployeePageResponse();
        employeePageResponse.setPageNumber(employeeEntities.getNumber());
        employeePageResponse.setPageSize(employeeEntities.getSize());
        employeePageResponse.setTotalElements(employeeEntities.getTotalElements());
        employeePageResponse.setTotalPages(employeeEntities.getTotalPages());
        employeePageResponse.setEmployees(employeeMapper.toListEmployeeResponse(employeeEntities.getContent()));

        return employeePageResponse;
    }

    /**
     * Kiểm tra username đã tồn tại trong hệ thống (dùng cho validate tạo mới nhân viên).
     * @param username tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa
     */
    @Override
    public boolean existsByUsername(String username) {
        if (username == null || username.trim().isEmpty())
            return false;
        return accountRepository.findByUsername(username.trim()).isPresent();
    }

    /**
     * Tạo mới một nhân viên.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo nhân viên mới.
     * @return Thực thể EmployeeEntity vừa được tạo.
     */
    @Transactional
    @Override
    public EmployeeEntity createEmployee(CreateEmployeeRequest request) {
        EmployeeEntity employee = new EmployeeEntity();
        employee.setFullName(request.getFullName().trim());
        employee.setPhoneNumber(request.getPhoneNumber().trim());
        employee.setAddress(request.getAddress().trim());
        employee.setIsDeleted(false);

        if (request.getPositionId() != null) {
            PositionEntity position = positionService.getPositionById(request.getPositionId());
            employee.setPosition(position);
        }

        if (StringUtils.hasText(request.getUsername()) && StringUtils.hasText(request.getPassword())) {
            AccountEntity account = new AccountEntity();
            account.setUsername(request.getUsername().trim());
            account.setPassword(passwordEncoder.encode(request.getPassword().trim()));
            account.setImageUrl(request.getImageUrl().trim());
            account.setIsDeleted(false);
            account.setPermission("EMPLOYEE");

            accountRepository.save(account);
            employee.setAccount(account);
        }

        return employeeRepository.save(employee);
    }

    /**
     * Xóa mềm một nhân viên (đặt isDeleted = true).
     *
     * @param id ID của nhân viên cần xóa.
     */
    @Transactional
    @Override
    public void deleteEmployee(Integer id) {
        EmployeeEntity employee = getEmployeeById(id);
        employee.setIsDeleted(true);

        employeeRepository.save(employee);
    }

    /**
     * Cập nhật thông tin nhân viên.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho nhân viên.
     */
    @Transactional
    @Override
    public void updateEmployee(UpdateEmployeeRequest request) {
        EmployeeEntity employee = getEmployeeById(request.getId());

        employee.setFullName(request.getFullName().trim());
        employee.setPhoneNumber(request.getPhoneNumber().trim());
        employee.setAddress(request.getAddress().trim());

        if (request.getPositionId() != null) {
            PositionEntity position = positionService.getPositionById(request.getPositionId());
            employee.setPosition(position);
        }

        if (StringUtils.hasText(request.getUsername()) && StringUtils.hasText(request.getPassword())) {
            String username = request.getUsername().trim();
            Optional<AccountEntity> optionalAccount = accountRepository.findByUsername(username);

            if (optionalAccount.isPresent()) {
                AccountEntity existingAccount = optionalAccount.get();

                if (!existingAccount.getEmployee().getId().equals(employee.getId())) {
                    throw new RuntimeException("Tên đăng nhập đã tồn tại cho nhân viên khác!");
                }

                existingAccount.setPassword(passwordEncoder.encode(request.getPassword()));
                existingAccount.setImageUrl(request.getImageUrl().trim());

                accountRepository.save(existingAccount);
            } else {
                AccountEntity newAccount = new AccountEntity();
                newAccount.setUsername(username);
                newAccount.setPassword(passwordEncoder.encode(request.getPassword()));
                newAccount.setPermission("EMPLOYEE");
                newAccount.setEmployee(employee);

                if (StringUtils.hasText(request.getImageUrl())) {
                    newAccount.setImageUrl(request.getImageUrl().trim());
                }

                accountRepository.save(newAccount);
                employee.setAccount(newAccount);
            }
        }
        employeeRepository.save(employee);
    }

    /**
     * Lấy thông tin nhân viên dựa trên id.
     *
     * @param id ID của nhân viên cần lấy thông tin.
     * @return Thực thể EmployeeEntity tương ứng với ID.
     */
    @Override
    public EmployeeEntity getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("Không tìm thấy nhân viên có id=" + id)));
    }
}