package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * AuthServiceImpl
 * Triển khai các chức năng xác thực cho hệ thống.
 * Thực hiện xác thực thông tin đăng nhập của người dùng,
 * lưu context xác thực vào session để duy trì trạng thái đăng nhập.
 */
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    // Quản lý xác thực
    private final AuthenticationManager authenticationManager;

    // Yêu cầu HTTP hiện tại
    private final HttpServletRequest request;

    /**
     * Xác thực thông tin đăng nhập của người dùng.
     *
     * @param username Tên đăng nhập của người dùng.
     * @param password Mật khẩu của người dùng.
     * @return true nếu thông tin đăng nhập hợp lệ, ngược lại false.
     * @throws RuntimeException Nếu thông tin đăng nhập không hợp lệ hoặc không đầy đủ
     */
    @Override
    public boolean login(String username, String password) {
        // Kiểm tra username và password không được để trống hoặc toàn dấu cách
        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin");
        }

        try {
            // Tạo đối tượng xác thực với username và password người dùng nhập
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            // Nếu xác thực thành công, lưu authentication vào SecurityContextHolder (bộ nhớ tạm của Spring Security)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu SecurityContext vào session để duy trì trạng thái đăng nhập của người dùng giữa các request
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            // Đăng nhập thành công
            return true;
        } catch (AuthenticationException e) {
            // Nếu xác thực thất bại (sai tài khoản hoặc mật khẩu), ném lỗi lên cho controller xử lý
            throw e;
        }
    }
}