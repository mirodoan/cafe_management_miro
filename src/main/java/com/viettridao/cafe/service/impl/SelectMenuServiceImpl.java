package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.sales.CreateSelectMenuRequest;
import com.viettridao.cafe.dto.response.sales.MenuItemResponse;
import com.viettridao.cafe.dto.response.sales.OrderDetailRessponse;
import com.viettridao.cafe.mapper.MenuItemMapper;
import com.viettridao.cafe.mapper.OrderDetailMapper;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.repository.*;
import com.viettridao.cafe.service.ReservationService;
import com.viettridao.cafe.service.SelectMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SelectMenuServiceImpl
 * Xử lý chọn món và tạo order cho bàn.
 */
@Service
@RequiredArgsConstructor
public class SelectMenuServiceImpl implements SelectMenuService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final MenuItemRepository menuItemRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderDetailMapper orderDetailMapper;
    private final ReservationService reservationService;

    /**
     * Tạo order mới cho bàn: kiểm tra trạng thái bàn, tạo hóa đơn/reservation/chi tiết hóa đơn, cập nhật trạng thái bàn.
     *
     * @param request    Thông tin chọn món.
     * @param employeeId ID nhân viên thực hiện.
     * @return OrderDetailRessponse kết quả order.
     */
    @Override
    @Transactional
    public OrderDetailRessponse createOrderForAvailableTable(CreateSelectMenuRequest request, Integer employeeId) {
        // Lọc những item hợp lệ (menuItemId != null và quantity > 0)
        List<CreateSelectMenuRequest.MenuOrderItem> items = request.getItems();
        if (items == null) items = new ArrayList<>();
        List<CreateSelectMenuRequest.MenuOrderItem> validItems = items.stream()
                .filter(item -> item.getMenuItemId() != null && item.getQuantity() != null && item.getQuantity() > 0)
                .toList();
        if (validItems.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một món và nhập số lượng.");
        }
        request.setItems(validItems);

        validateSelectMenuRequest(request);

        // Lấy bàn và kiểm tra trạng thái bàn
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại!"));

        // Lấy thông tin nhân viên
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));

        InvoiceEntity invoice;
        ReservationEntity reservation;

        switch (table.getStatus()) {
            case AVAILABLE -> {
                validateCustomerInfo(request);
                invoice = createNewInvoice();
                reservation = createNewReservation(table, employee, invoice, request);
                addInvoiceDetails(invoice, request.getItems());
                table.setStatus(TableStatus.OCCUPIED);
                tableRepository.save(table);
            }
            case RESERVED -> {
                reservation = getActiveReservation(table.getId(), "Không tìm thấy reservation cho bàn đã đặt!");
                invoice = reservation.getInvoice();
                invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
                invoiceRepository.save(invoice);
                table.setStatus(TableStatus.OCCUPIED);
                tableRepository.save(table);
                addOrUpdateInvoiceDetails(invoice, request.getItems());
            }
            case OCCUPIED -> {
                reservation = getActiveReservation(table.getId(), "Không tìm thấy reservation cho bàn đang sử dụng!");
                invoice = reservation.getInvoice();
                addOrUpdateInvoiceDetails(invoice, request.getItems());
            }
            default -> throw new IllegalStateException("Trạng thái bàn không hợp lệ!");
        }

        updateInvoiceTotalAmount(invoice);

        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());
        return orderDetailMapper.toOrderDetailResponse(table, invoice, reservation, invoiceDetails);
    }

    /**
     * Kiểm tra dữ liệu đầu vào cho chọn menu request
     */
    private void validateSelectMenuRequest(CreateSelectMenuRequest request) {
        if (request.getTableId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn bàn.");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một món.");
        }
    }

    /**
     * Kiểm tra thông tin khách hàng khi tạo mới order
     */
    private void validateCustomerInfo(CreateSelectMenuRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống khi tạo mới order.");
        }
        if (request.getCustomerPhone() == null || request.getCustomerPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại khách hàng không được để trống khi tạo mới order.");
        }
        // Có thể bổ sung validate format SĐT nếu cần
    }

    /**
     * Tạo mới hóa đơn
     */
    private InvoiceEntity createNewInvoice() {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setIsDeleted(false);
        invoiceRepository.save(invoice);
        return invoice;
    }

    /**
     * Tạo mới reservation
     */
    private ReservationEntity createNewReservation(TableEntity table, EmployeeEntity employee, InvoiceEntity invoice, CreateSelectMenuRequest request) {
        ReservationKey reservationKey = new ReservationKey();
        reservationKey.setIdTable(table.getId());
        reservationKey.setIdEmployee(employee.getId());
        reservationKey.setIdInvoice(invoice.getId());

        ReservationEntity reservation = new ReservationEntity();
        reservation.setId(reservationKey);
        reservation.setTable(table);
        reservation.setEmployee(employee);
        reservation.setInvoice(invoice);
        reservation.setCustomerName(request.getCustomerName());
        reservation.setCustomerPhone(request.getCustomerPhone());
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setIsDeleted(false);

        reservationRepository.save(reservation);
        return reservation;
    }

    /**
     * Lấy reservation đang hoạt động cho bàn
     */
    private ReservationEntity getActiveReservation(Integer tableId, String notFoundMsg) {
        return reservationRepository
                .findTopByTable_IdAndIsDeletedFalseOrderByReservationDateDesc(tableId)
                .orElseThrow(() -> new IllegalStateException(notFoundMsg));
    }

    /**
     * Thêm mới chi tiết hóa đơn
     */
    private void addInvoiceDetails(InvoiceEntity invoice, List<CreateSelectMenuRequest.MenuOrderItem> items) {
        for (CreateSelectMenuRequest.MenuOrderItem item : items) {
            MenuItemEntity menuItem = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Món ăn không tồn tại!"));
            if (Boolean.TRUE.equals(menuItem.getIsDeleted())) {
                throw new IllegalArgumentException("Món ăn đã ngừng kinh doanh.");
            }

            InvoiceKey invoiceKey = new InvoiceKey();
            invoiceKey.setIdInvoice(invoice.getId());
            invoiceKey.setIdMenuItem(menuItem.getId());

            InvoiceDetailEntity detail = new InvoiceDetailEntity();
            detail.setId(invoiceKey);
            detail.setInvoice(invoice);
            detail.setMenuItem(menuItem);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(menuItem.getCurrentPrice());
            detail.setIsDeleted(false);

            invoiceDetailRepository.save(detail);
        }
    }

    /**
     * Thêm mới hoặc cập nhật số lượng món trong chi tiết hóa đơn
     */
    private void addOrUpdateInvoiceDetails(InvoiceEntity invoice, List<CreateSelectMenuRequest.MenuOrderItem> items) {
        for (CreateSelectMenuRequest.MenuOrderItem item : items) {
            MenuItemEntity menuItem = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Món ăn không tồn tại!"));
            if (Boolean.TRUE.equals(menuItem.getIsDeleted())) {
                throw new IllegalArgumentException("Món ăn đã ngừng kinh doanh.");
            }

            InvoiceKey invoiceKey = new InvoiceKey();
            invoiceKey.setIdInvoice(invoice.getId());
            invoiceKey.setIdMenuItem(menuItem.getId());

            InvoiceDetailEntity detail = invoiceDetailRepository.findById(invoiceKey).orElse(null);

            if (detail != null && !Boolean.TRUE.equals(detail.getIsDeleted())) {
                // Tăng số lượng lên bằng hiện tại + số mới
                detail.setQuantity(detail.getQuantity() + item.getQuantity());
                invoiceDetailRepository.save(detail);
            } else {
                // Nếu chưa có thì thêm mới
                InvoiceDetailEntity newDetail = new InvoiceDetailEntity();
                newDetail.setId(invoiceKey);
                newDetail.setInvoice(invoice);
                newDetail.setMenuItem(menuItem);
                newDetail.setQuantity(item.getQuantity());
                newDetail.setPrice(menuItem.getCurrentPrice());
                newDetail.setIsDeleted(false);
                invoiceDetailRepository.save(newDetail);
            }
        }
    }

    /**
     * Tính và cập nhật tổng tiền hóa đơn
     */
    private void updateInvoiceTotalAmount(InvoiceEntity invoice) {
        List<InvoiceDetailEntity> details = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());

        double totalAmount = details.stream()
                .mapToDouble(detail -> detail.getPrice() * detail.getQuantity())
                .sum();

        invoice.setTotalAmount(totalAmount);
        invoiceRepository.save(invoice);
    }

    /**
     * Lấy danh sách món thực đơn chưa bị xóa.
     *
     * @return danh sách menu item response
     */
    @Override
    public List<MenuItemResponse> getMenuItems() {
        List<MenuItemEntity> menuEntities = menuItemRepository.findByIsDeletedFalseOrIsDeletedIsNull();
        return MenuItemMapper.toMenuItemResponseList(menuEntities);
    }

    /**
     * Chuẩn bị request chọn menu cho form
     */
    @Override
    public CreateSelectMenuRequest prepareSelectMenuRequest(Integer tableId) {
        if (tableId == null) {
            throw new IllegalArgumentException("Bạn chưa chọn bàn để thực hiện chức năng này!");
        }
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn với ID: " + tableId));
        CreateSelectMenuRequest selectMenuRequest = new CreateSelectMenuRequest();
        selectMenuRequest.setTableId(tableId);
        selectMenuRequest.setItems(new ArrayList<>());

        if (table.getStatus() == TableStatus.AVAILABLE) {
            // Nếu bàn trống, fill mặc định
            selectMenuRequest.setCustomerName("Khách vãng lai");
            selectMenuRequest.setCustomerPhone("0000000000");
        } else if (table.getStatus() == TableStatus.RESERVED || table.getStatus() == TableStatus.OCCUPIED) {
            ReservationEntity reservation = reservationService.findCurrentReservationByTableId(tableId);
            if (reservation != null) {
                selectMenuRequest.setCustomerName(reservation.getCustomerName());
                selectMenuRequest.setCustomerPhone(reservation.getCustomerPhone());
            }
        }
        return selectMenuRequest;
    }
}