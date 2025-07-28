package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.dto.request.reservation.RevenueFilterRequest;
import com.viettridao.cafe.dto.response.revenue.RevenueResponse;
import com.viettridao.cafe.dto.response.revenue.RevenueItemResponse;
import com.viettridao.cafe.model.EquipmentEntity;
import com.viettridao.cafe.model.ExpenseEntity;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.repository.EquipmentRepository;
import com.viettridao.cafe.repository.ExpenseRepository;
import com.viettridao.cafe.repository.ImportRepository;
import com.viettridao.cafe.repository.InvoiceRepository;
import com.viettridao.cafe.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * RevenueServiceImpl
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
@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {
    private final InvoiceRepository invoiceRepository;
    private final ImportRepository importRepository;
    private final EquipmentRepository equipmentRepository;
    private final ExpenseRepository expenseRepository;

    /**
     * Lấy tổng hợp doanh thu - chi tiêu theo ngày.
     *
     * @param request RevenueFilterRequest bộ lọc ngày.
     * @return RevenueResponse tổng hợp doanh thu/chi theo từng ngày.
     */
    @Override
    public RevenueResponse getRevenueSummary(RevenueFilterRequest request) {
        LocalDate start = request.getStartDate();
        LocalDate end = request.getEndDate();

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Ngày không hợp lệ");
        }

        Map<LocalDate, RevenueItemResponse> dailyMap = new TreeMap<>();

        // Thu nhập từ hóa đơn đã thanh toán
        for (InvoiceEntity invoice : invoiceRepository.findByCreatedAtBetweenAndIsDeletedFalseAndStatusEquals(start.atStartOfDay(), end.plusDays(1).atStartOfDay(), InvoiceStatus.PAID)) {
            if (invoice.getStatus() != InvoiceStatus.PAID) continue;
            LocalDate date = invoice.getCreatedAt().toLocalDate();
            dailyMap.putIfAbsent(date, new RevenueItemResponse());
            RevenueItemResponse item = dailyMap.get(date);
            item.setDate(date);
            item.setIncome(Optional.ofNullable(item.getIncome()).orElse(0.0) + invoice.getTotalAmount());
        }

        // Chi tiêu từ nhập hàng
        for (ImportEntity imp : importRepository.findByImportDateBetweenAndIsDeletedFalse(start, end)) {
            LocalDate date = imp.getImportDate();
            dailyMap.putIfAbsent(date, new RevenueItemResponse());
            RevenueItemResponse item = dailyMap.get(date);
            item.setDate(date);
            item.setExpense(Optional.ofNullable(item.getExpense()).orElse(0.0) + imp.getTotalAmount());
        }

        // Chi tiêu từ mua thiết bị
        for (EquipmentEntity equip : equipmentRepository.findByPurchaseDateBetweenAndIsDeletedFalse(start, end)) {
            LocalDate date = equip.getPurchaseDate();
            dailyMap.putIfAbsent(date, new RevenueItemResponse());
            RevenueItemResponse item = dailyMap.get(date);
            item.setDate(date);
            item.setExpense(Optional.ofNullable(item.getExpense()).orElse(0.0) + equip.getPurchasePrice());
        }

        // Chi tiêu từ các khoản chi phí khác
        for (ExpenseEntity expense : expenseRepository.findByExpenseDateBetweenAndIsDeletedFalse(start, end)) {
            LocalDate date = expense.getExpenseDate();
            dailyMap.putIfAbsent(date, new RevenueItemResponse());
            RevenueItemResponse item = dailyMap.get(date);
            item.setDate(date);
            item.setExpense(Optional.ofNullable(item.getExpense()).orElse(0.0) + expense.getAmount());
        }

        double totalIncome = dailyMap.values().stream().mapToDouble(i -> Optional.ofNullable(i.getIncome()).orElse(0.0)).sum();
        double totalExpense = dailyMap.values().stream().mapToDouble(i -> Optional.ofNullable(i.getExpense()).orElse(0.0)).sum();

        RevenueResponse response = new RevenueResponse();
        response.setSummaries(new ArrayList<>(dailyMap.values()));
        response.setTotalIncome(totalIncome);
        response.setTotalExpense(totalExpense);
        return response;
    }
}