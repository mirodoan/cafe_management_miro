package com.viettridao.cafe.controller;

import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.viettridao.cafe.dto.request.export.CreateExportRequest;
import com.viettridao.cafe.dto.request.imports.CreateImportRequest;
import com.viettridao.cafe.dto.response.warehouse_transaction.WarehouseTransactionPageResponse;
import com.viettridao.cafe.service.ExportService;
import com.viettridao.cafe.service.ImportService;
import com.viettridao.cafe.service.ProductService;
import com.viettridao.cafe.service.WarehouseTransactionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * WarehouseTransactionController
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
@RequestMapping("/warehouse/transaction")
public class WarehouseTransactionController {

    private final WarehouseTransactionService warehouseTransactionService;
    private final ImportService importService;
    private final ExportService exportService;
    private final ProductService productService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("")
    public String getAllTransactions(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        WarehouseTransactionPageResponse transactions = warehouseTransactionService.getTransactions(keyword, page, size);
        model.addAttribute("transactions", transactions);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPath", "/warehouse/transaction");
        return "/warehouses/transactions/transaction";
    }

    @GetMapping("/import/create")
    public String showFormCreateImport(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("import", new CreateImportRequest());
        return "/warehouses/transactions/create_import";
    }

    @PostMapping("/import/create")
    public String createImport(@Valid @ModelAttribute("import") CreateImportRequest request,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        // Lấy employeeId của nhân viên hiện tại từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer employeeId = employeeRepository.findByAccount_Username(username)
                .map(EmployeeEntity::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với username: " + username));
        request.setEmployeeId(employeeId);

        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "/warehouses/transactions/create_import";
        }
        try {
            importService.createImport(request);
            redirectAttributes.addFlashAttribute("success", "Thêm đơn nhập thành công");
            return "redirect:/warehouse/transaction";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/warehouse/transaction/import/create";
        }
    }

    @GetMapping("/export/create")
    public String showFormCreateExport(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("export", new CreateExportRequest());
        return "/warehouses/transactions/create_export";
    }

    @PostMapping("/export/create")
    public String createExport(@Valid @ModelAttribute("export") CreateExportRequest request,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        // Lấy employeeId của nhân viên hiện tại từ SecurityContext
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer employeeId = employeeRepository.findByAccount_Username(username)
                .map(EmployeeEntity::getId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên với username: " + username));
        request.setEmployeeId(employeeId);

        if (result.hasErrors()) {
            model.addAttribute("products", productService.getAllProducts());
            return "/warehouses/transactions/create_export";
        }

        try {
            exportService.createExport(request);
            redirectAttributes.addFlashAttribute("success", "Thêm đơn xuất thành công");
            return "redirect:/warehouse/transaction";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/warehouse/transaction/export/create";
        }
    }

    // API endpoint để lấy dữ liệu transactions cho AJAX
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<WarehouseTransactionPageResponse> getTransactionsApi(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        WarehouseTransactionPageResponse transactions = warehouseTransactionService.getTransactions(keyword, page, size);
        return ResponseEntity.ok(transactions);
    }

}