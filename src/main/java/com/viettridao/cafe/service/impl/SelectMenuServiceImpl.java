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
import com.viettridao.cafe.service.SelectMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * SelectMenuServiceImpl
 * Triển khai logic chọn món và tạo order cho bàn (SelectMenu).
 * Xử lý nghiệp vụ đặt món, tạo hóa đơn, reservation, cập nhật chi tiết hóa đơn.
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

    /**
     * Tạo order mới cho bàn: kiểm tra trạng thái bàn, tạo hóa đơn/reservation/chi tiết hóa đơn, cập nhật trạng thái bàn.
     *
     * @param request    thông tin chọn món.
     * @param employeeId id nhân viên thực hiện.
     * @return OrderDetailRessponse kết quả order.
     */
    @Override
    @Transactional
    public OrderDetailRessponse createOrderForAvailableTable(
            CreateSelectMenuRequest request, Integer employeeId) {
        // 1. Kiểm tra trạng thái bàn
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new IllegalArgumentException("Bàn không tồn tại!"));

        // 2. Lấy thông tin nhân viên từ DB
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));

        InvoiceEntity invoice = null;
        ReservationEntity reservation = null;

        if (table.getStatus() == TableStatus.AVAILABLE) {
            invoice = new InvoiceEntity();
            invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            invoice.setCreatedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            ReservationKey reservationKey = new ReservationKey();
            reservationKey.setIdTable(table.getId());
            reservationKey.setIdEmployee(employee.getId());
            reservationKey.setIdInvoice(invoice.getId());

            reservation = new ReservationEntity();
            reservation.setId(reservationKey);
            reservation.setTable(table);
            reservation.setEmployee(employee);
            reservation.setInvoice(invoice);
            reservation.setCustomerName(request.getCustomerName());
            reservation.setCustomerPhone(request.getCustomerPhone());
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setIsDeleted(false);
            reservationRepository.save(reservation);

            for (CreateSelectMenuRequest.MenuOrderItem item : request.getItems()) {
                MenuItemEntity menuItem = menuItemRepository.findById(item.getMenuItemId())
                        .orElseThrow(() -> new IllegalArgumentException("Món ăn không tồn tại!"));
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

            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        } else if (table.getStatus() == TableStatus.RESERVED) {
            reservation = reservationRepository.findAll().stream()
                    .filter(r -> r.getTable().getId().equals(table.getId()) && !Boolean.TRUE.equals(r.getIsDeleted()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy reservation cho bàn đã đặt!"));
            invoice = reservation.getInvoice();
            invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            invoiceRepository.save(invoice);
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
            updateOrAddInvoiceDetails(invoice, request);
        } else if (table.getStatus() == TableStatus.OCCUPIED) {
            reservation = reservationRepository.findAll().stream()
                    .filter(r -> r.getTable().getId().equals(table.getId()) && !Boolean.TRUE.equals(r.getIsDeleted()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Không tìm thấy reservation cho bàn đang sử dụng!"));
            invoice = reservation.getInvoice();
            updateOrAddInvoiceDetails(invoice, request);
        } else {
            throw new IllegalStateException("Trạng thái bàn không hợp lệ!");
        }

        updateInvoiceTotalAmount(invoice);

        List<InvoiceDetailEntity> invoiceDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());
        return orderDetailMapper.toOrderDetailResponse(table, invoice, reservation, invoiceDetails);
    }

    /**
     * Thêm mới hoặc cập nhật số lượng món trong chi tiết hóa đơn
     */
    private void updateOrAddInvoiceDetails(InvoiceEntity invoice, CreateSelectMenuRequest request) {
        for (CreateSelectMenuRequest.MenuOrderItem item : request.getItems()) {
            MenuItemEntity menuItem = menuItemRepository.findById(item.getMenuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Món ăn không tồn tại!"));
            InvoiceKey invoiceKey = new InvoiceKey();
            invoiceKey.setIdInvoice(invoice.getId());
            invoiceKey.setIdMenuItem(menuItem.getId());

            // Tìm chi tiết hóa đơn theo khóa chính (ID). Nếu không tìm thấy thì trả về null
            InvoiceDetailEntity detail = invoiceDetailRepository.findById(invoiceKey).orElse(null);

            // Nếu chi tiết hóa đơn đã tồn tại và chưa bị xóa
            if (detail != null && !Boolean.TRUE.equals(detail.getIsDeleted())) {
                // Tăng số lượng chi tiết hóa đơn lên bằng số lượng hiện tại + số lượng mới của món
                detail.setQuantity(detail.getQuantity() + item.getQuantity());
                invoiceDetailRepository.save(detail);
            } else {
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

}