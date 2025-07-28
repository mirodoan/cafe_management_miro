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

import com.viettridao.cafe.dto.request.product.CreateProductRequest;
import com.viettridao.cafe.dto.request.product.UpdateProductRequest;
import com.viettridao.cafe.mapper.ProductMapper;
import com.viettridao.cafe.service.ProductService;
import com.viettridao.cafe.service.UnitService;
import com.viettridao.cafe.model.ProductEntity;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * ProductController
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
@RequestMapping("/warehouse/product")
public class ProductController {
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final UnitService unitService;

    /**
     * Hiển thị danh sách sản phẩm với search và pagination.
     */
    @GetMapping("")
    public String home(@RequestParam(required = false) String keyword,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        model.addAttribute("products", productService.getAllProducts(keyword, page, size));
        model.addAttribute("currentPath", "/warehouse/product");
        return "/warehouses/products/product";
    }

    /**
     * Hiển thị form tạo sản phẩm mới.
     */
    @GetMapping("/create")
    public String showFormCreate(Model model) {
        model.addAttribute("units", unitService.getAllUnits());
        model.addAttribute("product", new CreateProductRequest());
        return "/warehouses/products/create_product";
    }

    /**
     * Xử lý submit form tạo sản phẩm mới.
     */
    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("product") CreateProductRequest product,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("units", unitService.getAllUnits());
                return "/warehouses/products/create_product";
            }
            // Kiểm tra trùng tên sản phẩm chưa bị xóa
            if (productService.existsByProductNameAndIsDeletedFalse(product.getProductName())) {
                result.rejectValue("productName", "error.productName", "Tên hàng hóa đã tồn tại");
                model.addAttribute("units", unitService.getAllUnits());
                return "/warehouses/products/create_product";
            }
            productService.createProduct(product);
            redirectAttributes.addFlashAttribute("success", "Thêm hàng hóa thành công");
            return "redirect:/warehouse/product";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/warehouse/product/create";
        }
    }

    /**
     * Xóa sản phẩm theo ID (soft delete).
     */
    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Xóa hàng hóa thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/warehouse/product";
    }

    /**
     * Hiển thị form cập nhật sản phẩm.
     */
    @GetMapping("/update/{id}")
    public String showFormUpdate(@PathVariable("id") Integer id,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            ProductEntity entity = productService.getProductById(id);
            UpdateProductRequest updateRequest = new UpdateProductRequest();
            updateRequest.setProductId(entity.getId());
            updateRequest.setProductName(entity.getProductName());
            // Lấy tên đơn vị tính từ entity nếu có, nếu không để rỗng
            if (entity.getUnit() != null) {
                updateRequest.setUnitName(entity.getUnit().getUnitName());
            } else {
                updateRequest.setUnitName("");
            }
            model.addAttribute("units", unitService.getAllUnits());
            model.addAttribute("product", updateRequest);
            return "/warehouses/products/update_product";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/warehouse/product";
        }
    }

    /**
     * Xử lý cập nhật sản phẩm.
     */
    @PostMapping("/update")
    public String updateProduct(@Valid @ModelAttribute("product") UpdateProductRequest request,
                                BindingResult result,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        try {
            if (result.hasErrors()) {
                model.addAttribute("units", unitService.getAllUnits());
                return "/warehouses/products/update_product";
            }
            // Kiểm tra trùng tên sản phẩm (trừ chính nó)
            if (productService.existsByProductNameAndIsDeletedFalseExceptId(request.getProductName(),
                    request.getProductId())) {
                result.rejectValue("productName", "error.productName", "Tên hàng hóa đã tồn tại");
                model.addAttribute("units", unitService.getAllUnits());
                return "/warehouses/products/update_product";
            }
            productService.updateProduct(request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật hàng hóa thành công");
            return "redirect:/warehouse/product";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/warehouse/product";
        }
    }
}