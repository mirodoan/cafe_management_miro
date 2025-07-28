package com.viettridao.cafe.mapper;

import com.viettridao.cafe.model.InvoiceEntity;
import com.viettridao.cafe.dto.response.expense.BudgetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

/**
 * IncomeMapper
 *
 * Version 1.0
 *
 * Date: 19-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 19-07-2025   mirodoan    Create
 *
 * Mapper chuyển đổi giữa InvoiceEntity và BudgetResponse (khoản thu).
 * - Chuyển ngày tạo (LocalDateTime) thành LocalDate.
 * - Ánh xạ tổng tiền hóa đơn sang income.
 */
@Mapper(componentModel = "spring")
public interface IncomeMapper {

    /**
     * Mapping từ InvoiceEntity sang BudgetResponse (income).
     * - date lấy từ createdAt và chuyển đổi sang LocalDate.
     * - income lấy từ totalAmount
     * - expense luôn là 0.0
     *
     * @param entity InvoiceEntity nguồn
     * @return BudgetResponse
     */
    @Mapping(target = "date", expression = "java(toLocalDate(entity.getCreatedAt()))")
    @Mapping(target = "income", source = "totalAmount")
    @Mapping(target = "expense", constant = "0.0")
    BudgetResponse fromInvoice(InvoiceEntity entity);

    /**
     * Helper chuyển LocalDateTime thành LocalDate.
     *
     * @param datetime LocalDateTime nguồn
     * @return LocalDate
     */
    default LocalDate toLocalDate(java.time.LocalDateTime datetime) {
        return datetime != null ? datetime.toLocalDate() : null;
    }
}