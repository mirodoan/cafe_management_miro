package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.account.UpdateAccountRequest;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.repository.AccountRepository;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * AccountServiceImpl
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
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    // Repository cho thực thể AccountEntity
    private final AccountRepository accountRepository;

    // Repository cho thực thể EmployeeEntity
    private final EmployeeRepository employeeRepository;

    // Bộ mã hóa mật khẩu
    private final PasswordEncoder passwordEncoder;

    /**
     * Cập nhật thông tin tài khoản.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho tài khoản.
     */
    @Transactional
    @Override
    public void updateAccount(UpdateAccountRequest request) {
        AccountEntity account = getAccountById(request.getId());

        if (StringUtils.hasText(request.getAddress()) ||
                StringUtils.hasText(request.getFullName()) ||
                StringUtils.hasText(request.getPhoneNumber())) {

            EmployeeEntity employee = account.getEmployee();

            if (employee == null) {
                employee = new EmployeeEntity();
                employee.setAccount(account);
            }

            // Cập nhật thông tin nhân viên liên kết với tài khoản
            employee.setFullName(request.getFullName());
            employee.setPhoneNumber(request.getPhoneNumber());
            employee.setAddress(request.getAddress());
            employee.setAccount(account);
            employeeRepository.save(employee);
            account.setImageUrl(request.getImageUrl());
            account.setEmployee(employee);
        }

        if (StringUtils.hasText(request.getPassword())) {
            // Mã hóa và cập nhật mật khẩu mới
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        accountRepository.save(account);
    }

    /**
     * Lấy thông tin tài khoản dựa trên ID.
     *
     * @param id ID của tài khoản cần lấy thông tin.
     * @return Thực thể AccountEntity tương ứng với ID.
     */
    @Override
    public AccountEntity getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản có id=" + id));
    }

    /**
     * Lấy thông tin tài khoản dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của tài khoản cần lấy thông tin.
     * @return Thực thể AccountEntity tương ứng với tên đăng nhập.
     */
    @Override
    public AccountEntity getAccountByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản có username=" + username));
    }
}