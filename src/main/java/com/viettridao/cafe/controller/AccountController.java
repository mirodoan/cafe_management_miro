package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.account.UpdateAccountRequest;
import com.viettridao.cafe.dto.response.account.AccountResponse;
import com.viettridao.cafe.mapper.AccountMapper;
import com.viettridao.cafe.service.AccountService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AccountController
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
@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {

    // Dependencies được inject - ưu tiên final fields để đảm bảo immutability
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    /**
     * Render trang thông tin tài khoản cho user đã xác thực.
     *
     * @param model Spring MVC model để binding với view
     * @return đường dẫn view cho template thông tin tài khoản
     */
    @GetMapping("")
    public String showAccountInfo(Model model) {
        // Lấy username hiện tại từ security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Lấy thông tin tài khoản từ service
        AccountResponse accountResponse = accountMapper.toAccountResponse(
                accountService.getAccountByUsername(currentUsername));

        // Đưa dữ liệu tài khoản ra view
        model.addAttribute("account", accountResponse != null ? accountResponse : new AccountResponse());

        return "/accounts/account";
    }

    /**
     * Xử lý cập nhật thông tin tài khoản qua POST request.
     *
     * @param request            DTO chứa thông tin tài khoản cập nhật từ form
     * @param redirectAttributes lưu trữ tạm thời cho success/error messages
     * @return redirect URL để ngăn form resubmission
     */
    @PostMapping("/update")
    public String updateAccountInfo(@Valid @ModelAttribute("account") UpdateAccountRequest request,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        // Nếu có lỗi validate, trả về lại view và truyền lỗi xuống frontend
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", request);
            return "/accounts/account";
        }
        try {
            // Gọi service cập nhật tài khoản
            accountService.updateAccount(request);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật thông tin cá nhân thành công!");

        } catch (Exception e) {
            // Thông báo lỗi
            redirectAttributes.addFlashAttribute("error",
                    "Cập nhật thất bại: " + e.getMessage());
        }

        // Redirect về trang tài khoản
        return "redirect:/account";
    }
}