package com.viettridao.cafe.service;

/**
 * AuthService
 * Lớp interface định nghĩa các chức năng xác thực cho hệ thống.
 * Chịu trách nhiệm xác thực thông tin đăng nhập của người dùng.
 */
public interface AuthService {

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return true nếu thông tin đăng nhập hợp lệ, ngược lại false.
     */
    boolean login(String username, String password);
}