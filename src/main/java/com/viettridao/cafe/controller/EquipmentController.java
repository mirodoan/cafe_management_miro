package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.equipment.CreateEquipmentRequest;
import com.viettridao.cafe.dto.request.equipment.UpdateEquipmentRequest;
import com.viettridao.cafe.dto.response.equipment.EquipmentPageResponse;
import com.viettridao.cafe.dto.response.equipment.EquipmentResponse;
import com.viettridao.cafe.mapper.EquipmentMapper;
import com.viettridao.cafe.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * EquipmentController
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
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;

    /**
     * Hiển thị danh sách thiết bị với pagination.
     *
     * @param page  số trang hiện tại (0-indexed)
     * @param size  số records mỗi trang
     * @param model Spring MVC model
     * @return view template danh sách thiết bị
     */
    @GetMapping("")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        EquipmentPageResponse equipmentPage = equipmentService.getAllEquipmentsPage(page, size);
        model.addAttribute("equipments", equipmentPage);
        model.addAttribute("equipmentList", equipmentPage.getEquipments());
        return "/equipments/equipment";
    }

    @GetMapping("/create")
    public String showFormCreate(Model model) {
        model.addAttribute("equipment", new CreateEquipmentRequest());
        return "/equipments/create_equipment";
    }

    @PostMapping("/create")
    public String createEquipment(@Valid @ModelAttribute("equipment") CreateEquipmentRequest equipment,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "/equipments/create_equipment";
            }
            equipmentService.createEquipment(equipment);
            redirectAttributes.addFlashAttribute("success", "Thêm thiết bị thành công");
            return "redirect:/equipment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/equipment/create";
        }
    }

    /**
     * Xóa thiết bị theo ID.
     *
     * @param id                 ID của thiết bị cần xóa
     * @param redirectAttributes flash attributes cho messages
     * @return redirect về danh sách thiết bị
     */
    @PostMapping("/delete/{id}")
    public String deleteEquipment(@PathVariable("id") Integer id,
                                  RedirectAttributes redirectAttributes) {
        try {
            equipmentService.deleteEquipment(id);
            redirectAttributes.addFlashAttribute("success", "Xóa thiết bị thành công");
            return "redirect:/equipment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/equipment";
        }
    }

    /**
     * Hiển thị form update thông tin thiết bị.
     *
     * @param id                 ID của thiết bị cần update
     * @param model              Spring MVC model
     * @param redirectAttributes flash attributes cho error handling
     * @return view template form update
     */
    @GetMapping("/update/{id}")
    public String showFormUpdate(@PathVariable("id") Integer id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            EquipmentResponse response = equipmentMapper.toEquipmentResponse(equipmentService.getEquipmentById(id));
            model.addAttribute("equipment", response);
            return "/equipments/update_equipment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/equipment";
        }
    }

    /**
     * Xử lý update thông tin thiết bị.
     *
     * @param request            update request DTO
     * @param result             validation binding result
     * @param redirectAttributes flash attributes
     * @return redirect URL hoặc view với errors
     */
    @PostMapping("/update")
    public String updateEquipment(@Valid @ModelAttribute UpdateEquipmentRequest request,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                return "/equipments/update_equipment";
            }
            equipmentService.updateEquipment(request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thiết bị thành công");
            return "redirect:/equipment";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/equipment";
        }
    }
}