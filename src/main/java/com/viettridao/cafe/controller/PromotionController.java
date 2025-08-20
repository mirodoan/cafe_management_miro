package com.viettridao.cafe.controller;

import com.viettridao.cafe.dto.request.promotion.CreatePromotionRequest;
import com.viettridao.cafe.dto.request.promotion.UpdatePromotionRequest;
import com.viettridao.cafe.dto.response.promotion.PromotionPageResponse;
import com.viettridao.cafe.mapper.PromotionMapper;
import com.viettridao.cafe.model.PromotionEntity;
import com.viettridao.cafe.service.PromotionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * PromotionController
 * Controller quản lý các chức năng liên quan đến khuyến mãi (Promotion).
 * Bao gồm: hiển thị danh sách khuyến mãi, tìm kiếm theo tên khuyến mãi, tạo mới, cập nhật, và xóa khuyến mãi.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/promotion")
public class PromotionController {

    // Service xử lý nghiệp vụ liên quan đến khuyến mãi
    private final PromotionService promotionService;

    // Mapper chuyển đổi giữa entity, DTO response
    private final PromotionMapper promotionMapper;

    /**
     * Hiển thị danh sách promotion đang có hiệu lực (phân trang).
     */
    @GetMapping("")
    public String getActivePromotions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size); // Tạo Pageable cho phân trang
        PromotionPageResponse promotions = promotionService.getValidPromotions(pageable, keyword); // Lấy promotions hợp lệ
        model.addAttribute("promotions", promotions.getPromotions());
        model.addAttribute("currentPage", promotions.getPageNumber());
        model.addAttribute("totalPages", promotions.getTotalPages());
        model.addAttribute("totalElements", promotions.getTotalElements());
        model.addAttribute("keyword", keyword); // Để giữ lại giá trị tìm kiếm trên giao diện
        return "promotions/promotion";
    }

    /**
     * Hiển thị form tạo promotion mới.
     */
    @GetMapping("/create")
    public String showFormCreate(Model model) {
        model.addAttribute("promotion", new CreatePromotionRequest());
        return "/promotions/create_promotion";
    }

    /**
     * Xử lý tạo promotion mới.
     */
    @PostMapping("/create")
    public String createPromotion(@Valid @ModelAttribute("promotion") CreatePromotionRequest promotion,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Nếu có lỗi toàn cục, gán vào endDate để hiển thị dưới input
            if (result.hasGlobalErrors()) {
                for (var error : result.getGlobalErrors()) {
                    result.rejectValue("endDate", error.getCode(), error.getDefaultMessage());
                }
            }
            if (result.hasErrors()) {
                return "/promotions/create_promotion";
            }
            promotionService.createPromotion(promotion);
            redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công");
            return "redirect:/promotion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotion/create";
        }
    }

    /**
     * Xóa promotion theo ID.
     */
    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable("id") Integer id,
                                  RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(id);
            redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công");
            return "redirect:/promotion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotion";
        }
    }

    /**
     * Hiển thị form update promotion.
     */
    @GetMapping("/update/{id}")
    public String showFormUpdate(@PathVariable("id") Integer id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            PromotionEntity entity = promotionService.getPromotionById(id);
            UpdatePromotionRequest dto = promotionMapper.toUpdatePromotionRequest(entity);
            model.addAttribute("promotion", dto);
            return "/promotions/update_promotion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotion";
        }
    }

    /**
     * Xử lý update promotion.
     */
    @PostMapping("/update")
    public String updatePromotion(@Valid @ModelAttribute("promotion") UpdatePromotionRequest request,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        try {
            // Nếu có lỗi toàn cục, gán vào endDate để hiển thị dưới input
            if (result.hasGlobalErrors()) {
                for (var error : result.getGlobalErrors()) {
                    result.rejectValue("endDate", error.getCode(), error.getDefaultMessage());
                }
            }
            if (result.hasErrors()) {
                model.addAttribute("promotion", request);
                return "/promotions/update_promotion";
            }
            promotionService.updatePromotion(request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công");
            return "redirect:/promotion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/promotion";
        }
    }
}