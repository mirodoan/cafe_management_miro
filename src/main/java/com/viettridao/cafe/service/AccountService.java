package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.account.UpdateAccountRequest;
import com.viettridao.cafe.model.AccountEntity;

/**
 * AccountService
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
public interface AccountService {

    /**
     * Cập nhật thông tin tài khoản.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho tài khoản.
     */
    void updateAccount(UpdateAccountRequest request);

    /**
     * Lấy thông tin tài khoản dựa trên ID.
     *
     * @param id ID của tài khoản cần lấy thông tin.
     * @return Thực thể AccountEntity tương ứng với ID.
     */
    AccountEntity getAccountById(Integer id);

    /**
     * Lấy thông tin tài khoản dựa trên tên đăng nhập.
     *
     * @param username Tên đăng nhập của tài khoản cần lấy thông tin.
     * @return Thực thể AccountEntity tương ứng với tên đăng nhập.
     */
    AccountEntity getAccountByUsername(String username);
}