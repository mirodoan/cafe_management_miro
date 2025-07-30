package com.viettridao.cafe.controller;

import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.sales.*;
import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;
import com.viettridao.cafe.mapper.OrderDetailMapper;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.AccountRepository;
import com.viettridao.cafe.repository.InvoiceDetailRepository;
import com.viettridao.cafe.repository.TableRepository;
import com.viettridao.cafe.service.ReservationService;
import com.viettridao.cafe.service.SelectMenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * SalesController
 * Controller quản lý các chức năng liên quan đến bán hàng (Sales) trong quán cafe.
 * Bao gồm: hiển thị danh sách bàn, đặt bàn, hủy bàn, chuyển bàn, gộp/tách bàn, chọn món, xem chi tiết order và thanh toán.
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/sale")
public class SalesController {

    // Repository quản lý thông tin bàn và trạng thái
    private final TableRepository tableRepository;
    // Service xử lý logic đặt bàn, gộp bàn, tách bàn, chuyển bàn
    private final ReservationService reservationService;
    // Repository quản lý thông tin tài khoản và nhân viên
    private final AccountRepository accountRepository;
    // Service xử lý logic chọn thực đơn và tạo order
    private final SelectMenuService selectMenuService;
    // Repository quản lý chi tiết hóa đơn (invoice details)
    private final InvoiceDetailRepository invoiceDetailRepository;
    // Mapper chuyển đổi entity sang DTO response
    private final OrderDetailMapper orderDetailMapper;

    @GetMapping("")
    public String getSalesOverview(Model model) {
        // Chuẩn bị dữ liệu cơ bản cho view
        model.addAttribute("tables", tableRepository.findAll());

        // Truyền sẵn object form cho các chức năng KHÔNG phụ thuộc bàn cụ thể
        if (!model.containsAttribute("reservation")) {
            model.addAttribute("reservation", new CreateReservationRequest());
        }
        if (!model.containsAttribute("selectMenuRequest")) {
            model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
        }

        // Các biến điều khiển hiển thị modal, mặc định ẩn hết khi mới vào trang
        model.addAttribute("showReservationForm", false);
        model.addAttribute("showSelectMenuForm", false);
        model.addAttribute("showMoveModal", false);
        model.addAttribute("showSplitModal", false);
        model.addAttribute("showMergeModal", false);
        model.addAttribute("showOrderDetailModal", false);
        model.addAttribute("showPaymentModal", false);
        model.addAttribute("showCancelReservationForm", false);

        return "sales/sales";
    }

    /**
     * Xem chi tiết order của bàn (Order Details Modal)
     */
    @GetMapping("/view-detail")
    public String getViewDetail(@RequestParam Integer tableId, Model model) {
        try {
            OrderDetailRessponse orderDetail = reservationService.getOrderDetailByTableId(tableId);
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("showOrderDetailModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("orderDetailError", e.getMessage());
            model.addAttribute("showOrderDetailModal", false);
        }
        model.addAttribute("tables", tableRepository.findAll());
        return "sales/sales";
    }

    /**
     * Hiển thị form thanh toán (Payment Modal)
     */
    @GetMapping("/show-payment-modal")
    public String showPaymentModal(@RequestParam Integer tableId, Model model) {
        try {
            OrderDetailRessponse orderDetail = reservationService.getPaymentInfoForTable(tableId);
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("showPaymentModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        model.addAttribute("tables", tableRepository.findAll());
        return "sales/sales";
    }

    /**
     * Xác nhận thanh toán (Pay Invoice)
     */
    @PostMapping("/pay-invoice")
    public String payInvoice(@RequestParam Integer tableId, Model model, RedirectAttributes redirectAttributes) {
        try {
            reservationService.payInvoiceForTable(tableId);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
        model.addAttribute("tables", tableRepository.findAll());
        return "sales/sales";
    }

    /**
     * Hiển thị form chọn thực đơn (Select Menu)
     */
    @GetMapping("/show-select-menu-form")
    public String showSelectMenuForm(@RequestParam(value = "tableId", required = false) Integer tableId, Model model) {
        try {
            CreateSelectMenuRequest selectMenuRequest = selectMenuService.prepareSelectMenuRequest(tableId);
            model.addAttribute("selectMenuRequest", selectMenuRequest);
            model.addAttribute("selectedTable", tableRepository.findById(tableId).orElse(null));
            model.addAttribute("tables", tableRepository.findAll());
            model.addAttribute("showSelectMenuForm", true);
            model.addAttribute("menuItems", selectMenuService.getMenuItems());
            return "sales/sales";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("tables", tableRepository.findAll());
            return "sales/sales";
        }
    }

    /**
     * Xử lý chọn thực đơn trên sales
     */
    @PostMapping("/select-menu-on-sales")
    public String selectMenuOnSales(
            @Valid @ModelAttribute("selectMenuRequest") CreateSelectMenuRequest request,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Trả về form với lỗi validate dữ liệu hình thức
            prepareSelectMenuFormModel(model, request);
            return "sales/sales";
        }

        try {
            Integer employeeId = getCurrentEmployeeId();
            OrderDetailRessponse orderDetail = selectMenuService.createOrderForAvailableTable(request, employeeId);
            model.addAttribute("tables", tableRepository.findAll());
            model.addAttribute("successMessage", "Chọn món thành công!");
            model.addAttribute("orderDetail", orderDetail);
            model.addAttribute("showSelectMenuForm", false);
            return "sales/sales";
        } catch (IllegalArgumentException e) {
            // Lỗi nghiệp vụ: gắn vào model để hiển thị trên form
            model.addAttribute("errorMessage", e.getMessage());
            prepareSelectMenuFormModel(model, request);
            return "sales/sales";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            prepareSelectMenuFormModel(model, request);
            return "sales/sales";
        }
    }

    /**
     * Helper method để gắn lại các attribute cần thiết cho form
     */
    private void prepareSelectMenuFormModel(Model model, CreateSelectMenuRequest request) {
        model.addAttribute("tables", tableRepository.findAll());
        model.addAttribute("selectMenuRequest", request);
        model.addAttribute("selectedTable", request.getTableId() != null ? tableRepository.findById(request.getTableId()).orElse(null) : null);
        model.addAttribute("showSelectMenuForm", true);
        model.addAttribute("menuItems", selectMenuService.getMenuItems());
    }

    /**
     * Hiển thị form đặt bàn (Reservation)
     */
    @GetMapping("/show-reservation-form")
    public String showReservationForm(@RequestParam Integer tableId, Model model, RedirectAttributes redirectAttributes) {
        // 1. Kiểm tra xem bàn có tồn tại và còn khả dụng không
        Optional<TableEntity> tableOpt = tableRepository.findById(tableId);
        if (tableOpt.isEmpty() || tableOpt.get().getStatus() != TableStatus.AVAILABLE) {
            // 2. Nếu bàn không khả dụng, trả về thông báo lỗi và quay lại trang danh sách bàn
            redirectAttributes.addFlashAttribute("errorMessage", "Bàn không tồn tại hoặc đã được đặt/chưa sẵn sàng!");
            return "redirect:/sale";
        }
        // 3. Nếu bàn hợp lệ, khởi tạo request đặt bàn với tableId đã chọn
        CreateReservationRequest reservation = new CreateReservationRequest();
        reservation.setTableId(tableId);
        reservation.setTableName(tableOpt.get().getTableName());
        // 4. Đẩy dữ liệu cần thiết ra view để hiển thị form
        prepareReservationForm(model, reservation, true);
        return "sales/sales";
    }

    /**
     * Xử lý tạo reservation (đặt bàn)
     */
    @PostMapping("/reservations")
    public String createReservation(
            @Valid @ModelAttribute("reservation") CreateReservationRequest request,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // 1. Nếu có lỗi validate dữ liệu đầu vào, trả lại form với thông tin và lỗi
        if (bindingResult.hasErrors()) {
            // Lấy lại tên bàn
            if (request.getTableId() != null) {
                Optional<TableEntity> tableOpt = tableRepository.findById(request.getTableId());
                tableOpt.ifPresent(table -> request.setTableName(table.getTableName()));
            }
            prepareReservationForm(model, request, true);
            return "sales/sales";
        }
        try {
            // 2. Lấy employeeId của nhân viên đang đăng nhập
            Integer employeeId = getCurrentEmployeeId();
            // 3. Gọi service để tạo mới reservation (đặt bàn)
            reservationService.createReservation(request, employeeId);
            // 4. Nếu thành công, trả về thông báo và reload lại trang sale
            redirectAttributes.addFlashAttribute("successMessage", "Đặt bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            // 5. Nếu có lỗi nghiệp vụ (bàn không còn khả dụng, ...), hiển thị lỗi lên form
            bindingResult.reject("reservation", e.getMessage());
        } catch (Exception e) {
            // 6. Nếu có lỗi hệ thống, cũng hiển thị lỗi lên form
            bindingResult.reject("reservation", "Lỗi hệ thống: " + e.getMessage());
        }
        // 7. Nếu có lỗi, trả lại form với dữ liệu vừa nhập và lỗi tương ứng
        prepareReservationForm(model, request, true);
        return "sales/sales";
    }

    /**
     * Helper: Chuẩn bị dữ liệu cho form đặt bàn
     */
    private void prepareReservationForm(Model model, CreateReservationRequest reservation, boolean showForm) {
        // Lấy ngày hiện tại
        model.addAttribute("today", LocalDate.now());
        // Lấy lại danh sách bàn cho view
        model.addAttribute("tables", tableRepository.findAll());
        // Gán lại object form reservation
        model.addAttribute("reservation", reservation);
        // Gán biến điều khiển hiển thị modal đặt bàn
        model.addAttribute("showReservationForm", showForm);
    }

    /**
     * Xử lý hủy bàn (Cancel Reservation)
     */
    @GetMapping("/show-cancel-reservation-form")
    public String showCancelReservationForm(@RequestParam Integer tableId, Model model) {
        TableEntity table = tableRepository.findById(tableId).orElse(null);
        ReservationEntity reservation = reservationService.findCurrentReservationByTableId(tableId);
        model.addAttribute("tables", tableRepository.findAll());
        model.addAttribute("selectedTable", table);
        model.addAttribute("reservation", reservation);
        model.addAttribute("showCancelReservationForm", true);
        return "sales/sales";
    }

    @PostMapping("/cancel-reservation")
    public String cancelReservation(@RequestParam Integer tableId, Model model) {
        try {
            reservationService.cancelReservation(tableId);
            model.addAttribute("tables", tableRepository.findAll());
            model.addAttribute("successMessage", "Hủy bàn thành công!");
            model.addAttribute("showCancelReservationForm", false);
        } catch (IllegalArgumentException e) {
            TableEntity table = tableRepository.findById(tableId).orElse(null);
            ReservationEntity reservation = reservationService.findCurrentReservationByTableId(tableId);
            model.addAttribute("tables", tableRepository.findAll());
            model.addAttribute("selectedTable", table);
            model.addAttribute("reservation", reservation);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("showCancelReservationForm", true); // Giữ modal mở lại
        }
        return "sales/sales";
    }

    /**
     * Hiển thị form chuyển bàn (Move Table)
     */
    @GetMapping("/show-move-table-form")
    public String showMoveTableForm(@RequestParam Integer selectedTableId, Model model) {
        try {
            Map<String, Object> formData = reservationService.prepareMoveTableForm(selectedTableId);
            model.addAttribute("showMoveModal", true);
            model.addAttribute("selectedTableId", selectedTableId);
            model.addAttribute("tables", formData.get("tables"));
            model.addAttribute("sourceTable", formData.get("sourceTable"));
            model.addAttribute("availableTables", formData.get("availableTables"));
            model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("tables", tableRepository.findAll());
            return "sales/sales";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi khi thiết lập form chuyển bàn: " + e.getMessage());
            model.addAttribute("tables", tableRepository.findAll());
            return "sales/sales";
        }
        return "sales/sales";
    }

    /**
     * Xử lý chuyển bàn (Move Table)
     */
    @PostMapping("/move-table")
    public String moveTable(
            @RequestParam(required = false) Integer sourceTableId,
            @RequestParam(required = false) Integer targetTableId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            if (sourceTableId == null) throw new IllegalArgumentException("Không tìm thấy bàn nguồn");
            if (targetTableId == null) throw new IllegalArgumentException("Vui lòng chọn bàn đích");
            MoveTableRequest request = new MoveTableRequest();
            request.setSourceTableId(sourceTableId);
            request.setTargetTableId(targetTableId);
            Integer employeeId = getCurrentEmployeeId();
            reservationService.moveTable(request, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Chuyển bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
        // Lấy lại dữ liệu cho form nếu lỗi
        try {
            Map<String, Object> formData = reservationService.prepareMoveTableForm(sourceTableId);
            model.addAttribute("showMoveModal", true);
            model.addAttribute("selectedTableId", sourceTableId);
            model.addAttribute("tables", formData.get("tables"));
            model.addAttribute("sourceTable", formData.get("sourceTable"));
            model.addAttribute("availableTables", formData.get("availableTables"));
            model.addAttribute("reservation", new CreateReservationRequest());
            model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
        } catch (Exception ex) {
            model.addAttribute("tables", tableRepository.findAll());
        }
        return "sales/sales";
    }

    /**
     * Hiển thị form gộp bàn (Merge Table)
     */
    @GetMapping("/show-merge-table-form")
    public String showMergeTableForm(@RequestParam Integer selectedTableId, Model model) {
        // Lấy danh sách bàn đang OCCUPIED để chọn gộp
        List<TableEntity> occupiedTables = tableRepository.findAll().stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED)
                .toList();

        // Đẩy dữ liệu ra view form gộp bàn
        model.addAttribute("showMergeModal", true);
        model.addAttribute("occupiedTables", occupiedTables);
        model.addAttribute("selectedTableId", selectedTableId);

        return "sales/sales";
    }

    /**
     * Xử lý gộp bàn (Merge Tables)
     */
    @PostMapping("/merge-tables")
    public String mergeTables(@Valid @ModelAttribute("mergeTableRequest") MergeTableRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return prepareMergeTableForm(model, request, bindingResult);
        }
        try {
            Integer employeeId = getCurrentEmployeeId();
            reservationService.mergeTables(request, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Gộp bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
        return prepareMergeTableForm(model, request, bindingResult);
    }

    private String prepareMergeTableForm(Model model, MergeTableRequest request, BindingResult bindingResult) {
        List<TableEntity> occupiedTables = tableRepository.findAll().stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED)
                .toList();
        model.addAttribute("mergeTableRequest", request);
        model.addAttribute("showMergeModal", true);
        model.addAttribute("occupiedTables", occupiedTables);
        model.addAttribute("org.springframework.validation.BindingResult.mergeTableRequest", bindingResult);
        return "sales/sales";
    }

    /**
     * Hiển thị form tách bàn (Split Table)
     */
    @GetMapping("/show-split-table-form")
    public String showSplitTableForm(@RequestParam Integer selectedTableId, Model model) {
        try {
            // Gọi service để lấy dữ liệu cho form, trả về Map<String, Object>
            Map<String, Object> formData = reservationService.prepareSplitTableForm(selectedTableId);
            model.addAllAttributes(formData);
            model.addAttribute("showSplitModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "sales/sales";
    }

    /**
     * Xử lý tách bàn (Split Table)
     */
    @PostMapping("/split-table")
    public String splitTable(@Valid @ModelAttribute SplitTableRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return setupSplitModalOnError(request, model, bindingResult, "Lỗi validation dữ liệu đầu vào");
        }
        try {
            Integer employeeId = getCurrentEmployeeId();
            reservationService.splitTable(request, employeeId);
            redirectAttributes.addFlashAttribute("successMessage", "Tách bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            return setupSplitModalOnError(request, model, bindingResult, e.getMessage());
        } catch (RuntimeException e) {
            return setupSplitModalOnError(request, model, bindingResult, "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        }
    }

    /**
     * Helper: Setup lại modal tách bàn khi có lỗi (giữ lại input của user)
     */
    private String setupSplitModalOnError(SplitTableRequest request, Model model,
                                          BindingResult bindingResult, String errorMessage) {
        try {
            Map<String, Object> formData = reservationService.prepareSplitTableForm(request.getSourceTableId());
            model.addAllAttributes(formData);
            model.addAttribute("showSplitModal", true);
            model.addAttribute("splitTableRequest", request); // giữ lại input người dùng
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("org.springframework.validation.BindingResult.splitTableRequest", bindingResult);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Lỗi hệ thống khi hiển thị form: " + e.getMessage());
        }
        return "sales/sales";
    }

    /**
     * Helper: Lấy ID nhân viên hiện tại từ session đăng nhập
     */
    private Integer getCurrentEmployeeId() {
        // Lấy username từ Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        AccountEntity account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản cho username: " + username));
        if (account.getEmployee() == null) {
            throw new IllegalArgumentException("Tài khoản '" + username + "' không liên kết với nhân viên nào!");
        }
        return account.getEmployee().getId();
    }
}