package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.auth.LoginRequest;
import com.viettridao.cafe.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController
 * Controller xử lý các request liên quan đến đăng nhập (login) cho người dùng.
 * Quản lý hiển thị form đăng nhập và xử lý logic xác thực tài khoản.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class AuthController {

    // AuthService để xử lý business logic authentication
    private final AuthService authService;

    /**
     * Hiển thị form đăng nhập cho user.
     *
     * @return đường dẫn tới login template
     */
    @GetMapping("")
    public String showLoginForm(Model model) {
        model.addAttribute("login", new LoginRequest());
        // Trả về login form template
        return "auth/login";
    }

    /**
     * Xử lý đăng nhập cho user.
     *
     * @param request            chứa thông tin đăng nhập của user
     * @param bindingResult      để kiểm tra lỗi validation
     * @param redirectAttributes để truyền thông báo giữa các request
     * @return đường dẫn tới trang chủ hoặc về lại trang đăng nhập
     */
    @PostMapping("")
    public String login(@Valid @ModelAttribute("login") LoginRequest request,
                        BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                // Trả về lại trang login với lỗi validation
                return "auth/login";
            }
            boolean result = authService.login(request.getUsername(), request.getPassword());
            if (result) {
                redirectAttributes.addFlashAttribute("success", "Đăng nhập thành công!");
                return "redirect:/home";
            }
        } catch (IllegalArgumentException e) {
            // Lỗi thiếu thông tin đầu vào
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/login";
        } catch (AuthenticationException e) {
            // Lỗi xác thực (sai tài khoản, mật khẩu, user không tồn tại)
            redirectAttributes.addFlashAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            return "redirect:/login";
        } catch (Exception e) {
            // Lỗi không xác định
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi, vui lòng thử lại sau");
            return "redirect:/login";
        }
        redirectAttributes.addFlashAttribute("error", "Đăng nhập thất bại");
        return "redirect:/login";
    }
}