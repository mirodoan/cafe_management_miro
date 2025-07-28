package com.viettridao.cafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viettridao.cafe.dto.request.employee.CreateEmployeeRequest;
import com.viettridao.cafe.dto.request.employee.UpdateEmployeeRequest;
import com.viettridao.cafe.dto.response.employee.EmployeeResponse;
import com.viettridao.cafe.mapper.EmployeeMapper;
import com.viettridao.cafe.mapper.PositionMapper;
import com.viettridao.cafe.service.EmployeeService;
import com.viettridao.cafe.service.PositionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * EmployeeController
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
@RequestMapping("/employee")
public class EmployeeController {

    // Core dependencies - injected via constructor
    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;
    private final PositionService positionService;
    private final PositionMapper positionMapper;

    /**
     * Hiển thị danh sách nhân viên với tính năng search và pagination.
     *
     * @param keyword từ khóa tìm kiếm (optional)
     * @param page    số trang hiện tại (default 0)
     * @param size    số record mỗi trang (default 10)
     * @param model   Spring MVC model
     * @return view template danh sách nhân viên
     */
    @GetMapping("")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees(keyword, page, size));
        return "/employees/employee";
    }

    // Hiển thị form tạo nhân viên mới
    @GetMapping("/create")
    public String showFormCreate(Model model) {
        model.addAttribute("positions", positionMapper.toListPositionResponse(positionService.getPositions()));
        model.addAttribute("employee", new CreateEmployeeRequest());
        return "/employees/create_employee";
    }

    // Xử lý submit form tạo nhân viên mới
    @PostMapping("/create")
    public String createEmployee(@Valid @ModelAttribute("employee") CreateEmployeeRequest employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("positions", positionMapper.toListPositionResponse(positionService.getPositions()));
                return "/employees/create_employee";
            }
            if (employeeService.existsByUsername(employee.getUsername())) {
                result.rejectValue("username", "error.username", "Tên đăng nhập đã tồn tại");
                model.addAttribute("positions", positionMapper.toListPositionResponse(positionService.getPositions()));
                return "/employees/create_employee";
            }
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("success", "Thêm nhân viên thành công");
            return "redirect:/employee";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee/create";
        }
    }

    /**
     * Xóa nhân viên theo ID.
     *
     * @param id                 ID của nhân viên cần xóa
     * @param redirectAttributes flash attributes cho messages
     * @return redirect về danh sách nhân viên
     */
    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable("id") Integer id,
                                 RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Xóa nhân viên thành công");
            return "redirect:/employee";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee";
        }
    }

    /**
     * Hiển thị form cập nhật thông tin nhân viên.
     *
     * @param id                 ID của nhân viên cần update
     * @param model              Spring MVC model
     * @param redirectAttributes flash attributes cho error handling
     * @return view template form update hoặc redirect
     */
    @GetMapping("/update/{id}")
    public String showFormUpdate(@PathVariable("id") Integer id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            EmployeeResponse response = employeeMapper.toEmployeeResponse(employeeService.getEmployeeById(id));
            model.addAttribute("positions", positionMapper.toListPositionResponse(positionService.getPositions()));
            model.addAttribute("employee", response);
            return "/employees/update_employee";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee";
        }
    }

    /**
     * Xử lý cập nhật thông tin nhân viên.
     *
     * @param request            update request DTO
     * @param result             validation result
     * @param redirectAttributes flash attributes
     * @param model              Spring MVC model
     * @return redirect URL
     */
    @PostMapping("/update")
    public String updateEmployee(@Valid @ModelAttribute("employee") UpdateEmployeeRequest request,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("positions", positionMapper.toListPositionResponse(positionService.getPositions()));
                return "/employees/update_employee";
            }
            employeeService.updateEmployee(request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công");
            return "redirect:/employee";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/employee";
        }
    }
}