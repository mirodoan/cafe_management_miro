package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.menu_item.CreateMenuItemRequest;
import com.viettridao.cafe.dto.request.menu_item.UpdateMenuItemRequest;
import com.viettridao.cafe.dto.response.menu_item.MenuItemResponse;
import com.viettridao.cafe.mapper.MenuMapper;
import com.viettridao.cafe.service.MenuItemService;
import com.viettridao.cafe.service.ProductService;
import com.viettridao.cafe.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MenuItemController
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
@RequestMapping("/menu")
public class MenuItemController {

    private final MenuItemService menuItemService;
    private final MenuMapper menuMapper;
    private final ProductService productService;
    private final UnitService unitService;

    @GetMapping("")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        // Hiển thị danh sách thực đơn, hỗ trợ tìm kiếm và phân trang
        model.addAttribute("menus", menuItemService.getAllMenuItems(keyword, page, size));
        return "/menu/menu";
    }

    @GetMapping("/create")
    public String showFormCreate(Model model) {
        // Nạp danh sách sản phẩm & đơn vị tính cho form tạo mới
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("units", unitService.getAllUnitResponses());
        model.addAttribute("menu", new CreateMenuItemRequest());
        return "/menu/create_menu";
    }

    @PostMapping("/create")
    public String createMenu(@Valid @ModelAttribute("menu") CreateMenuItemRequest request, BindingResult result,
                             RedirectAttributes redirectAttributes, Model model) {
        try {
            if (result.hasErrors()) {
                // Nếu có lỗi validate, nạp lại dữ liệu cho form
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/create_menu";
            }
            // Kiểm tra trùng tên thực đơn
            if (menuItemService.existsByItemName(request.getItemName())) {
                result.rejectValue("itemName", "error.itemName", "Tên thực đơn đã tồn tại");
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/create_menu";
            }
            // Validate nguyên liệu - phải có ít nhất 1 nguyên liệu
            if (request.getMenuDetails() == null || request.getMenuDetails().isEmpty()) {
                result.reject("error.menuDetails", "Phải có ít nhất một nguyên liệu");
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/create_menu";
            }
            menuItemService.createMenuItem(request);
            redirectAttributes.addFlashAttribute("success", "Thêm thực đơn thành công");
            return "redirect:/menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/menu";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMenu(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuItemService.deleteMenuItem(id);
            redirectAttributes.addFlashAttribute("success", "Xoá thực đơn thành công");
            return "redirect:/menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/menu";
        }
    }

    @GetMapping("/update/{id}")
    public String showFormUpdate(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            MenuItemResponse response = menuMapper.toResponse(menuItemService.getMenuItemById(id));
            // Chuyển đổi response sang request để bind form
            UpdateMenuItemRequest request = new UpdateMenuItemRequest();
            request.setId(response.getId());
            request.setItemName(response.getItemName());
            request.setCurrentPrice(response.getCurrentPrice());
            request.setMenuDetails(menuMapper.toMenuDetailRequestList(response.getMenuDetails()));

            model.addAttribute("products", productService.getAllProducts());
            model.addAttribute("units", unitService.getAllUnitResponses());
            model.addAttribute("menu", request);
            return "/menu/update_menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/menu";
        }
    }

    @PostMapping("/update")
    public String updateMenu(@Valid @ModelAttribute("menu") UpdateMenuItemRequest request, BindingResult result,
                             RedirectAttributes redirectAttributes, Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/update_menu";
            }
            // Kiểm tra trùng tên thực đơn (không tính thực đơn hiện tại)
            if (menuItemService.existsByItemNameAndIdNot(request.getItemName(), request.getId())) {
                result.rejectValue("itemName", "error.itemName", "Tên thực đơn đã tồn tại");
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/update_menu";
            }
            // Validate nguyên liệu - phải có ít nhất 1 nguyên liệu
            if (request.getMenuDetails() == null || request.getMenuDetails().isEmpty()) {
                result.reject("error.menuDetails", "Phải có ít nhất một nguyên liệu");
                model.addAttribute("products", productService.getAllProducts());
                model.addAttribute("units", unitService.getAllUnitResponses());
                model.addAttribute("menu", request);
                return "/menu/update_menu";
            }
            menuItemService.updateMenuItem(request);
            redirectAttributes.addFlashAttribute("success", "Chỉnh sửa thực đơn thành công");
            return "redirect:/menu";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/menu";
        }
    }

}