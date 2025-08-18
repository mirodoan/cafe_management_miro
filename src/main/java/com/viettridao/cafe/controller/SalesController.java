package com.viettridao.cafe.controller;

import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.sales.*;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.TableEntity;
import com.viettridao.cafe.repository.AccountRepository;
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

    private final TableRepository tableRepository;
    private final ReservationService reservationService;
    private final AccountRepository accountRepository;
    private final SelectMenuService selectMenuService;

    /**
     * Hiển thị trang tổng quan bán hàng.
     *
     * @param model Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("")
    public String getSalesOverview(Model model) {
        // Chuẩn bị dữ liệu hiển thị mặc định cho view
        prepareDefaultViewData(model);
        return "sales/sales";
    }

    /**
     * Xem chi tiết order của 1 bàn.
     *
     * @param tableId Id của bàn cần xem
     * @param model   Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/view-detail")
    public String getViewDetail(@RequestParam Integer tableId, Model model) {
        // Lấy thông tin chi tiết order của bàn, nếu lỗi thì set thông báo lỗi
        try {
            model.addAttribute("orderDetail", reservationService.getOrderDetailByTableId(tableId));
            model.addAttribute("showOrderDetailModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("orderDetailError", e.getMessage());
        }
        // Hiển thị danh sách bàn ở sidebar
        prepareTableList(model);
        return "sales/sales";
    }

    /**
     * Hiện modal thanh toán cho bàn đã chọn.
     *
     * @param tableId Id bàn cần thanh toán
     * @param model   Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-payment-modal")
    public String showPaymentModal(@RequestParam Integer tableId, Model model) {
        // Lấy dữ liệu hóa đơn cho modal thanh toán, nếu lỗi thì set thông báo lỗi
        try {
            model.addAttribute("orderDetail", reservationService.getPaymentInfoForTable(tableId));
            model.addAttribute("showPaymentModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }
        prepareTableList(model);
        return "sales/sales";
    }

    /**
     * Xử lý thanh toán hóa đơn cho bàn.
     *
     * @param tableId            Id bàn cần thanh toán
     * @param model              Model để truyền dữ liệu cho view nếu có lỗi
     * @param redirectAttributes Redirect để truyền thông báo thành công
     * @return Redirect về /sale hoặc trả về view nếu lỗi
     */
    @PostMapping("/pay-invoice")
    public String payInvoice(@RequestParam Integer tableId, Model model, RedirectAttributes redirectAttributes) {
        // Xử lý thanh toán hóa đơn, nếu thành công thì redirect về danh sách bàn
        try {
            reservationService.payInvoiceForTable(tableId);
            redirectAttributes.addFlashAttribute("success", "Thanh toán thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            prepareTableList(model);
            return "sales/sales";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            prepareTableList(model);
            return "sales/sales";
        }
    }

    /**
     * Hiện form chọn món cho bàn.
     *
     * @param tableId Id bàn cần chọn món (có thể null)
     * @param model   Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-select-menu-form")
    public String showSelectMenuForm(@RequestParam(value = "tableId", required = false) Integer tableId, Model model) {
        try {
            TableEntity table = tableRepository.findById(tableId).orElse(null);
            model.addAttribute("selectedTable", table);
            if (table != null) {
                model.addAttribute("selectedTableStatus", table.getStatus().name()); // Truyền status là String
            }
            prepareSelectMenuFormModel(model, selectMenuService.prepareSelectMenuRequest(tableId));
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            prepareTableList(model);
        }
        return "sales/sales";
    }

    /**
     * Xử lý khi chọn món trên trang bán hàng.
     *
     * @param request       Request chứa thông tin món chọn
     * @param bindingResult BindingResult để kiểm tra lỗi form
     * @param model         Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @PostMapping("/select-menu-on-sales")
    public String selectMenuOnSales(
            @ModelAttribute("selectMenuRequest") CreateSelectMenuRequest request,
            BindingResult bindingResult,
            Model model) {

        // Validate các trường khách hàng
        if (request.getCustomerName() != null && !request.getCustomerName().trim().isEmpty()) {
            if (request.getCustomerName().trim().length() < 3) {
                bindingResult.rejectValue("customerName", "error.customerName", "Tên khách hàng tối thiểu 3 ký tự");
            }
        }
        if (request.getCustomerPhone() != null && !request.getCustomerPhone().trim().isEmpty()) {
            if (!request.getCustomerPhone().matches("^0\\d{9,10}$")) {
                bindingResult.rejectValue("customerPhone", "error.customerPhone", "Số điện thoại phải bắt đầu từ 0 và có từ 10 đến 11 chữ số");
            }
        }

        // Set mặc định nếu để trống
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            request.setCustomerName("Khách vãng lai");
        }
        if (request.getCustomerPhone() == null || request.getCustomerPhone().trim().isEmpty()) {
            request.setCustomerPhone("0000000000");
        }

        // Kiểm tra lỗi
        if (bindingResult.hasErrors()) {
            // Lỗi validate form (các trường khách hàng)
            prepareSelectMenuFormModel(model, request);
            // Modal chọn món vẫn mở
            return "sales/sales";
        }
        try {
            Integer employeeId = getCurrentEmployeeId();
            model.addAttribute("orderDetail", selectMenuService.createOrderForAvailableTable(request, employeeId));
            model.addAttribute("success", "Chọn món thành công!");
            model.addAttribute("showSelectMenuForm", false);
        } catch (RuntimeException e) {
            // Đúng ở đây: set lỗi vào menuError thay vì error
            model.addAttribute("menuError", e.getMessage());
            prepareSelectMenuFormModel(model, request);
            // Modal chọn món vẫn mở
        }
        prepareTableList(model);
        return "sales/sales";
    }

    /**
     * Hiện form đặt bàn cho bàn còn trống.
     *
     * @param tableId            Id bàn cần đặt
     * @param model              Model để truyền dữ liệu cho view
     * @param redirectAttributes Redirect để chuyển thông báo lỗi nếu có
     * @return Tên view sales/sales hoặc redirect về /sale nếu lỗi
     */
    @GetMapping("/show-reservation-form")
    public String showReservationForm(@RequestParam Integer tableId, Model model, RedirectAttributes redirectAttributes) {
        Optional<TableEntity> tableOpt = tableRepository.findById(tableId);
        // Kiểm tra trạng thái bàn trước khi cho đặt
        if (tableOpt.isEmpty() || tableOpt.get().getStatus() != TableStatus.AVAILABLE) {
            redirectAttributes.addFlashAttribute("error", "Bàn không tồn tại hoặc đã được đặt/chưa sẵn sàng!");
            return "redirect:/sale";
        }
        // Chuẩn bị dữ liệu cho form đặt bàn
        CreateReservationRequest reservation = new CreateReservationRequest();
        reservation.setTableId(tableId);
        reservation.setTableName(tableOpt.get().getTableName());
        prepareReservationForm(model, reservation, true);
        return "sales/sales";
    }

    /**
     * Xử lý tạo mới đặt bàn.
     *
     * @param request            Thông tin đặt bàn từ form
     * @param bindingResult      Kết quả validate form
     * @param model              Model để truyền dữ liệu cho view
     * @param redirectAttributes Redirect để chuyển thông báo thành công
     * @return Tên view sales/sales hoặc redirect về /sale nếu thành công
     */
    @PostMapping("/reservations")
    public String createReservation(@Valid @ModelAttribute("reservation") CreateReservationRequest request,
                                    BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi validate form đặt bàn
        if (bindingResult.hasErrors()) {
            if (request.getTableId() != null) {
                tableRepository.findById(request.getTableId())
                        .ifPresent(table -> request.setTableName(table.getTableName()));
            }
            prepareReservationForm(model, request, true);
            return "sales/sales";
        }
        try {
            // Tạo mới thông tin đặt bàn và thông báo thành công
            Integer employeeId = getCurrentEmployeeId();
            reservationService.createReservation(request, employeeId);
            redirectAttributes.addFlashAttribute("success", "Đặt bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            bindingResult.reject("reservation", e.getMessage());
            prepareReservationForm(model, request, true);
            return "sales/sales";
        } catch (RuntimeException e) {
            bindingResult.reject("reservation", e.getMessage());
            prepareReservationForm(model, request, true);
            return "sales/sales";
        }
    }

    /**
     * Hiện form hủy đặt bàn.
     *
     * @param tableId Id bàn cần hủy đặt
     * @param model   Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-cancel-reservation-form")
    public String showCancelReservationForm(@RequestParam Integer tableId, Model model) {
        // Lấy thông tin bàn và reservation hiện tại
        TableEntity table = tableRepository.findById(tableId).orElse(null);
        ReservationEntity reservation = reservationService.findCurrentReservationByTableId(tableId);
        prepareTableList(model);
        model.addAttribute("selectedTable", table);
        model.addAttribute("reservation", reservation);
        model.addAttribute("showCancelReservationForm", true);
        return "sales/sales";
    }

    /**
     * Xử lý hủy đặt bàn.
     *
     * @param tableId Id bàn cần hủy
     * @param model   Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @PostMapping("/cancel-reservation")
    public String cancelReservation(@RequestParam Integer tableId, Model model) {
        // Hủy đặt bàn, nếu lỗi thì hiển thị lại form hủy bàn và thông báo lỗi
        try {
            reservationService.cancelReservation(tableId);
            model.addAttribute("success", "Hủy bàn thành công!");
            model.addAttribute("showCancelReservationForm", false);
        } catch (IllegalArgumentException e) {
            TableEntity table = tableRepository.findById(tableId).orElse(null);
            ReservationEntity reservation = reservationService.findCurrentReservationByTableId(tableId);
            model.addAttribute("selectedTable", table);
            model.addAttribute("reservation", reservation);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("showCancelReservationForm", true);
        }
        prepareTableList(model);
        return "sales/sales";
    }

    /**
     * Hiện form chuyển bàn.
     *
     * @param selectedTableId Id bàn nguồn cần chuyển
     * @param model           Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-move-table-form")
    public String showMoveTableForm(@RequestParam Integer selectedTableId, Model model) {
        // Chuẩn bị dữ liệu cho form chuyển bàn, nếu lỗi thì set thông báo lỗi
        try {
            Map<String, Object> formData = reservationService.prepareMoveTableForm(selectedTableId);
            model.addAllAttributes(formData);
            model.addAttribute("showMoveModal", true);
            model.addAttribute("selectedTableId", selectedTableId);
            model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            prepareTableList(model);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            prepareTableList(model);
        }
        return "sales/sales";
    }

    /**
     * Xử lý chuyển bàn.
     *
     * @param sourceTableId      Id bàn nguồn
     * @param targetTableId      Id bàn đích
     * @param model              Model để truyền dữ liệu cho view
     * @param redirectAttributes Redirect để chuyển thông báo thành công
     * @return Tên view sales/sales hoặc redirect về /sale nếu thành công
     */
    @PostMapping("/move-table")
    public String moveTable(@RequestParam(required = false) Integer sourceTableId,
                            @RequestParam(required = false) Integer targetTableId,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        // Kiểm tra đầu vào và thực hiện chuyển bàn, nếu lỗi thì load lại form chuyển bàn
        try {
            if (sourceTableId == null) throw new IllegalArgumentException("Không tìm thấy bàn nguồn");
            if (targetTableId == null) throw new IllegalArgumentException("Vui lòng chọn bàn đích");
            MoveTableRequest request = new MoveTableRequest();
            request.setSourceTableId(sourceTableId);
            request.setTargetTableId(targetTableId);
            Integer employeeId = getCurrentEmployeeId();
            reservationService.moveTable(request, employeeId);
            redirectAttributes.addFlashAttribute("success", "Chuyển bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            try {
                Map<String, Object> formData = reservationService.prepareMoveTableForm(sourceTableId);
                model.addAllAttributes(formData);
                model.addAttribute("showMoveModal", true);
                model.addAttribute("selectedTableId", sourceTableId);
                model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
            } catch (Exception ex) {
                prepareTableList(model);
            }
            return "sales/sales";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            try {
                Map<String, Object> formData = reservationService.prepareMoveTableForm(sourceTableId);
                model.addAllAttributes(formData);
                model.addAttribute("showMoveModal", true);
                model.addAttribute("selectedTableId", sourceTableId);
                model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
            } catch (Exception ex) {
                prepareTableList(model);
            }
            return "sales/sales";
        }
    }

    /**
     * Hiện form gộp bàn.
     *
     * @param selectedTableId Id bàn đang chọn để gộp
     * @param model           Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-merge-table-form")
    public String showMergeTableForm(
            @RequestParam(value = "selectedTableId", required = false) Integer selectedTableId, Model model) {
        // Lấy danh sách bàn đang sử dụng để gộp bàn
        List<TableEntity> occupiedTables = tableRepository.findAll().stream()
                .filter(table -> table.getStatus() == TableStatus.OCCUPIED)
                .toList();
        model.addAttribute("showMergeModal", true);
        model.addAttribute("occupiedTables", occupiedTables);
        model.addAttribute("selectedTableId", selectedTableId); // Có thể null
        return "sales/sales";
    }

    /**
     * Xử lý gộp bàn.
     *
     * @param request            Request chứa thông tin gộp bàn
     * @param bindingResult      Kết quả validate form
     * @param model              Model để truyền dữ liệu cho view
     * @param redirectAttributes Redirect để chuyển thông báo thành công
     * @return Tên view sales/sales hoặc redirect về /sale nếu thành công
     */
    @PostMapping("/merge-tables")
    public String mergeTables(@Valid @ModelAttribute("mergeTableRequest") MergeTableRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi validate form gộp bàn
        if (bindingResult.hasErrors()) {
            return prepareMergeTableForm(model, request, bindingResult);
        }
        try {
            // Gọi service thực hiện gộp bàn và thông báo thành công
            Integer employeeId = getCurrentEmployeeId();
            reservationService.mergeTables(request, employeeId);
            redirectAttributes.addFlashAttribute("success", "Gộp bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return prepareMergeTableForm(model, request, bindingResult);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return prepareMergeTableForm(model, request, bindingResult);
        }
    }

    /**
     * Chuẩn bị lại dữ liệu cho form gộp bàn khi có lỗi.
     *
     * @param model         Model để truyền dữ liệu cho view
     * @param request       Request chứa thông tin gộp bàn
     * @param bindingResult Kết quả validate form
     * @return Tên view sales/sales
     */
    private String prepareMergeTableForm(Model model, MergeTableRequest request, BindingResult bindingResult) {
        // Lấy lại danh sách bàn đang sử dụng và gán vào model
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
     * Hiện form tách bàn.
     *
     * @param selectedTableId Id bàn cần tách
     * @param model           Model để truyền dữ liệu cho view
     * @return Tên view sales/sales
     */
    @GetMapping("/show-split-table-form")
    public String showSplitTableForm(@RequestParam Integer selectedTableId, Model model) {
        // Chuẩn bị dữ liệu form tách bàn, nếu lỗi thì set thông báo lỗi
        try {
            model.addAllAttributes(reservationService.prepareSplitTableForm(selectedTableId));
            model.addAttribute("showSplitModal", true);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "sales/sales";
    }

    /**
     * Xử lý tách bàn.
     *
     * @param request            Request tách bàn
     * @param bindingResult      Kết quả validate form
     * @param model              Model để truyền dữ liệu cho view
     * @param redirectAttributes Redirect để chuyển thông báo thành công
     * @return Tên view sales/sales hoặc redirect về /sale nếu thành công
     */
    @PostMapping("/split-table")
    public String splitTable(@Valid @ModelAttribute SplitTableRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi validate form tách bàn
        if (bindingResult.hasErrors()) {
            return setupSplitModalOnError(request, model, bindingResult, "Lỗi validation dữ liệu đầu vào");
        }
        try {
            // Gọi service thực hiện tách bàn và thông báo thành công
            Integer employeeId = getCurrentEmployeeId();
            reservationService.splitTable(request, employeeId);
            redirectAttributes.addFlashAttribute("success", "Tách bàn thành công!");
            return "redirect:/sale";
        } catch (IllegalArgumentException e) {
            return setupSplitModalOnError(request, model, bindingResult, e.getMessage());
        } catch (RuntimeException e) {
            return setupSplitModalOnError(request, model, bindingResult, e.getMessage());
        }
    }

    /**
     * Set lại dữ liệu hiển thị khi tách bàn bị lỗi.
     *
     * @param request       Request tách bàn
     * @param model         Model để truyền dữ liệu cho view
     * @param bindingResult Kết quả validate form
     * @param errorMessage  Thông báo lỗi
     * @return Tên view sales/sales
     */
    private String setupSplitModalOnError(SplitTableRequest request, Model model,
                                          BindingResult bindingResult, String errorMessage) {
        // Gán lại dữ liệu và hiển thị thông báo lỗi lên form tách bàn
        try {
            model.addAllAttributes(reservationService.prepareSplitTableForm(request.getSourceTableId()));
            model.addAttribute("showSplitModal", true);
            model.addAttribute("splitTableRequest", request);
            model.addAttribute("error", errorMessage);
            model.addAttribute("org.springframework.validation.BindingResult.splitTableRequest", bindingResult);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi hệ thống khi hiển thị form: " + e.getMessage());
        }
        return "sales/sales";
    }

    /**
     * Chuẩn bị dữ liệu cho form chọn món.
     *
     * @param model   Model để truyền dữ liệu cho view
     * @param request Request chọn món
     */
    private void prepareSelectMenuFormModel(Model model, CreateSelectMenuRequest request) {
        // Lấy danh sách bàn, menu và thông tin bàn chọn vào model
        prepareTableList(model);
        model.addAttribute("selectMenuRequest", request);
        model.addAttribute("selectedTable", request.getTableId() != null
                ? tableRepository.findById(request.getTableId()).orElse(null) : null);
        model.addAttribute("showSelectMenuForm", true);
        model.addAttribute("menuItems", selectMenuService.getMenuItems());
    }

    /**
     * Chuẩn bị dữ liệu cho form đặt bàn.
     *
     * @param model       Model để truyền dữ liệu cho view
     * @param reservation Request đặt bàn
     * @param showForm    Có hiển thị form hay không
     */
    private void prepareReservationForm(Model model, CreateReservationRequest reservation, boolean showForm) {
        // Gán lại các biến dùng cho form đặt bàn
        model.addAttribute("today", LocalDate.now());
        prepareTableList(model);
        model.addAttribute("reservation", reservation);
        model.addAttribute("showReservationForm", showForm);
    }

    /**
     * Chuẩn bị dữ liệu mặc định cho view sales.
     *
     * @param model Model để truyền dữ liệu cho view
     */
    private void prepareDefaultViewData(Model model) {
        // Gán các biến mặc định cho các modal và list bàn
        prepareTableList(model);
        if (!model.containsAttribute("reservation"))
            model.addAttribute("reservation", new CreateReservationRequest());
        if (!model.containsAttribute("selectMenuRequest"))
            model.addAttribute("selectMenuRequest", new CreateSelectMenuRequest());
        model.addAttribute("showReservationForm", false);
        model.addAttribute("showSelectMenuForm", false);
        model.addAttribute("showMoveModal", false);
        model.addAttribute("showSplitModal", false);
        model.addAttribute("showMergeModal", false);
        model.addAttribute("showOrderDetailModal", false);
        model.addAttribute("showPaymentModal", false);
        model.addAttribute("showCancelReservationForm", false);
    }

    /**
     * Lấy danh sách bàn và gán vào model.
     *
     * @param model Model để truyền dữ liệu cho view
     */
    private void prepareTableList(Model model) {
        // Lấy toàn bộ danh sách bàn từ DB và gán vào model
        model.addAttribute("tables", tableRepository.findAll());
    }

    /**
     * Lấy employeeId từ user đăng nhập hiện tại.
     *
     * @return employeeId của nhân viên đang đăng nhập, hoặc ném IllegalArgumentException nếu không tìm thấy hoặc chưa liên kết nhân viên
     * @throws IllegalArgumentException nếu không tìm thấy account hoặc account chưa gắn nhân viên
     */
    private Integer getCurrentEmployeeId() {
        // Lấy authentication từ SecurityContext để xác định user đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Lấy username từ authentication
        String username = authentication.getName();
        // Tìm account theo username, nếu không có thì throw
        AccountEntity account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản cho username: " + username));
        // Nếu account chưa gắn với nhân viên thì throw
        if (account.getEmployee() == null)
            throw new IllegalArgumentException("Tài khoản '" + username + "' không liên kết với nhân viên nào!");
        // Trả về id của nhân viên liên kết với account
        return account.getEmployee().getId();
    }
}