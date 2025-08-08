package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.account.UpdateAccountRequest;
import com.viettridao.cafe.dto.response.account.AccountResponse;
import com.viettridao.cafe.mapper.AccountMapper;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.service.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        model.addAttribute("editMode", false);

        return "/accounts/account";
    }

    /**
     * Hiển thị trang chỉnh sửa thông tin tài khoản cho user đã xác thực.
     * Route này render view với trạng thái "editMode=true" để các trường có thể chỉnh sửa.
     */
    @GetMapping("/edit")
    public String editAccountInfo(Model model) {
        // Lấy thông tin xác thực hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();

        // Lấy thông tin tài khoản từ service
        AccountResponse accountResponse = accountMapper.toAccountResponse(
                accountService.getAccountByUsername(currentUsername));

        // Đưa dữ liệu tài khoản vào model (phục vụ binding trong form)
        model.addAttribute("account", accountResponse != null ? accountResponse : new AccountResponse());

        // Đặt biến editMode = true để view chuyển sang chế độ chỉnh sửa
        model.addAttribute("editMode", true);

        // Trả về view Thymeleaf hiển thị form chỉnh sửa
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
                                    Model model,
                                    HttpSession session) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("account", request);
            model.addAttribute("editMode", true);
            return "/accounts/account";
        }
        try {
            // 1. Cập nhật DB qua service
            accountService.updateAccount(request);

            // 2. Lấy lại tài khoản vừa cập nhật (bạn nên lấy theo username đăng nhập hiện tại)
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            String username = currentAuth.getName();
            AccountEntity updatedAccount = accountService.getAccountByUsername(username);

            // 3. Cập nhật lại principal trong SecurityContext
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    updatedAccount, // principal mới
                    currentAuth.getCredentials(),
                    updatedAccount.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // 4. (Nếu header lấy từ session) Cập nhật lại user trong session
            session.setAttribute("user", updatedAccount);

            redirectAttributes.addFlashAttribute("success",
                    "Cập nhật thông tin cá nhân thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Cập nhật thất bại: " + e.getMessage());
        }
        return "redirect:/account";
    }
}