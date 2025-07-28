package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.mapper.ExpenseMapper;
import com.viettridao.cafe.repository.AccountRepository;
import com.viettridao.cafe.repository.EquipmentRepository;
import com.viettridao.cafe.repository.ExpenseRepository;
import com.viettridao.cafe.repository.InvoiceRepository;
import com.viettridao.cafe.service.BudgetService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.dto.request.expense.BudgetFilterRequest;
import com.viettridao.cafe.dto.request.expense.ExpenseRequest;
import com.viettridao.cafe.dto.response.expense.BudgetResponse;
import com.viettridao.cafe.mapper.EquipmentMapper;
import com.viettridao.cafe.mapper.IncomeMapper;
import com.viettridao.cafe.model.AccountEntity;
import com.viettridao.cafe.model.EquipmentEntity;
import com.viettridao.cafe.model.ExpenseEntity;
import com.viettridao.cafe.model.InvoiceEntity;

/**
 * BudgetServiceImpl
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
 * Triển khai Service cho thực thể Budget (Ngân sách).
 * Xử lý nghiệp vụ liên quan đến tổng hợp doanh thu, chi phí, thiết bị.
 */
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private final ExpenseRepository expenseRepository;
    private final InvoiceRepository invoiceRepository;
    private final EquipmentRepository equipmentRepository;
    private final AccountRepository accountRepository;

    private final ExpenseMapper expenseMapper;
    private final IncomeMapper incomeMapper;
    private final EquipmentMapper equipmentMapper;

    /**
     * Lấy tổng hợp ngân sách gồm doanh thu, chi phí, thiết bị theo bộ lọc thời gian và phân trang.
     *
     * @param request Bộ lọc thời gian và phân trang.
     * @return Page<BudgetResponse> danh sách ngân sách tổng hợp.
     */
    @Override
    public Page<BudgetResponse> getBudgetView(BudgetFilterRequest request) {
        LocalDate from = request.getFromDate();
        LocalDate to = request.getToDate();

        List<InvoiceEntity> invoices = invoiceRepository.findByStatusAndCreatedAtBetween(
                InvoiceStatus.PAID,
                from.atStartOfDay(), to.plusDays(1).atStartOfDay());

        List<ExpenseEntity> expenses = expenseRepository.findExpensesBetweenDates(from, to);
        List<EquipmentEntity> equipmentList = equipmentRepository.findEquipmentsBetweenDates(from, to);

        List<BudgetResponse> incomeDtos = invoices.stream().map(incomeMapper::fromInvoice)
                .collect(Collectors.toList());

        List<BudgetResponse> expenseDtos = expenses.stream().map(expenseMapper::toDto).collect(Collectors.toList());

        List<BudgetResponse> equipmentDtos = equipmentList.stream().map(equipmentMapper::toBudgetDto)
                .collect(Collectors.toList());

        Map<LocalDate, BudgetResponse> merged = new HashMap<>();

        for (BudgetResponse income : incomeDtos) {
            merged.put(income.getDate(), new BudgetResponse(income.getDate(), income.getIncome(), 0.0));
        }

        expenseDtos.forEach(dto -> mergeExpense(merged, dto));
        equipmentDtos.forEach(dto -> mergeExpense(merged, dto));

        List<BudgetResponse> result = new ArrayList<>(merged.values());
        result.sort(Comparator.comparing(BudgetResponse::getDate).reversed());

        int start = request.getPage() * request.getSize();
        if (start >= result.size()) {
            return Page.empty(PageRequest.of(request.getPage(), request.getSize()));
        }
        int end = Math.min(start + request.getSize(), result.size());
        List<BudgetResponse> pageContent = result.subList(start, end);

        return new PageImpl<>(pageContent, PageRequest.of(request.getPage(), request.getSize()), result.size());
    }

    /**
     * Gộp chi phí vào bản ghi ngân sách theo ngày.
     */
    private void mergeExpense(Map<LocalDate, BudgetResponse> map, BudgetResponse dto) {
        map.merge(dto.getDate(), dto, (oldVal, newVal) -> {
            oldVal.setExpense(oldVal.getExpense() + newVal.getExpense());
            return oldVal;
        });
    }

    /**
     * Thêm mới chi phí (Expense) cho tài khoản.
     *
     * @param request  thông tin chi phí cần thêm.
     * @param username tên đăng nhập tài khoản.
     */
    @Override
    public void addExpense(ExpenseRequest request, String username) {
        AccountEntity account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));

        ExpenseEntity entity = expenseMapper.fromRequest(request);
        entity.setAccount(account);
        expenseRepository.save(entity);
    }
}