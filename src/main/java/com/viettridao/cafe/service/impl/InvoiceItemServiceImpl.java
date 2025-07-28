package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.dto.request.invoice.InvoiceItemListRequest;
import com.viettridao.cafe.dto.request.invoice.InvoiceItemRequest;
import com.viettridao.cafe.dto.response.invoice.InvoiceItemResponse;
import com.viettridao.cafe.mapper.InvoiceDetailMapper;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.repository.*;
import com.viettridao.cafe.service.InvoiceItemService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * InvoiceItemServiceImpl
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
 *
 * Triển khai Service cho nghiệp vụ thêm món vào hóa đơn.
 * Xử lý kiểm tra trạng thái bàn, hóa đơn, thêm món, cập nhật tổng tiền và trạng thái bàn.
 */
@Service
@RequiredArgsConstructor
public class InvoiceItemServiceImpl implements InvoiceItemService {
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceRepository invoiceRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepository;
    private final InvoiceDetailMapper invoiceDetailMapper;

    /**
     * Thêm các món vào hóa đơn chỉ định, kiểm tra trạng thái, tạo mới hóa đơn nếu cần, cập nhật tổng tiền.
     *
     * @param request danh sách món ăn cần thêm vào hóa đơn.
     * @return danh sách món đã thêm vào hóa đơn (InvoiceItemResponse).
     */
    @Override
    @Transactional
    public List<InvoiceItemResponse> addItemsToInvoice(InvoiceItemListRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sách món không được để trống");
        }

        Integer invoiceId = request.getItems().get(0).getInvoiceId();
        InvoiceEntity invoice = invoiceRepository.findById(invoiceId).orElse(null);

        TableEntity table = tableRepository.findByReservations_Invoice_Id(invoiceId);
        if (table == null) {
            throw new RuntimeException("Không tìm thấy bàn liên kết với hóa đơn ID: " + invoiceId);
        }

        if (!(table.getStatus() == TableStatus.AVAILABLE || table.getStatus() == TableStatus.RESERVED
                || table.getStatus() == TableStatus.OCCUPIED)) {
            throw new RuntimeException("Bàn không cho phép thêm món");
        }

        if (invoice == null || invoice.getStatus() != InvoiceStatus.PENDING_PAYMENT) {
            invoice = new InvoiceEntity();
            invoice.setStatus(InvoiceStatus.PENDING_PAYMENT);
            invoice.setTotalAmount(0.0);
            invoice.setCreatedAt(LocalDateTime.now());
            invoice.setIsDeleted(false);
            invoice = invoiceRepository.save(invoice);

            ReservationEntity latestReservation = reservationRepository
                    .findTopByTable_IdAndIsDeletedFalseOrderByReservationDateDesc(table.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn gần nhất để gắn hóa đơn"));

            latestReservation.setInvoice(invoice);
            latestReservation.getId().setIdInvoice(invoice.getId());
            reservationRepository.save(latestReservation);
        }

        double total = 0.0;
        for (InvoiceItemRequest itemReq : request.getItems()) {
            MenuItemEntity menuItem = menuItemRepository.findById(itemReq.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy món với ID: " + itemReq.getMenuItemId()));

            InvoiceKey key = new InvoiceKey();
            key.setIdInvoice(invoice.getId());
            key.setIdMenuItem(menuItem.getId());

            InvoiceDetailEntity detail = invoiceDetailRepository.findByIdAndIsDeletedFalse(key).orElse(null);

            if (detail != null) {
                detail.setQuantity(detail.getQuantity() + itemReq.getQuantity());
            } else {
                detail = invoiceDetailMapper.fromRequest(itemReq);
                detail.setInvoice(invoice);
                detail.setMenuItem(menuItem);
                detail.setPrice(menuItem.getCurrentPrice());
            }

            detail.setIsDeleted(false);
            invoiceDetailRepository.save(detail);
        }

        List<InvoiceDetailEntity> allDetails = invoiceDetailRepository
                .findAllByInvoice_IdAndIsDeletedFalse(invoice.getId());
        for (InvoiceDetailEntity detail : allDetails) {
            total += detail.getQuantity() * detail.getPrice();
        }
        invoice.setTotalAmount(total);
        invoiceRepository.save(invoice);

        table.setStatus(TableStatus.OCCUPIED);
        tableRepository.save(table);

        return invoiceDetailMapper.toDtoList(allDetails);
    }
}