package com.viettridao.cafe.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.viettridao.cafe.dto.request.expense.ExpenseRequest;
import com.viettridao.cafe.dto.response.expense.BudgetResponse;
import com.viettridao.cafe.model.ExpenseEntity;

/**
 * ExpenseMapper
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
 * Mapper chuyển đổi giữa ExpenseEntity, ExpenseRequest và BudgetResponse sử dụng MapStruct.
 */
@Mapper(componentModel = "spring")
public interface ExpenseMapper {

	/**
	 * Mapping từ ExpenseEntity sang BudgetResponse.
	 * - date lấy từ expenseDate
	 * - expense lấy từ amount
	 * - income luôn là 0.0
	 */
	@Mapping(target = "date", source = "expenseDate")
	@Mapping(target = "expense", source = "amount")
	@Mapping(target = "income", constant = "0.0")
	BudgetResponse toDto(ExpenseEntity entity);

	/**
	 * Mapping từ ExpenseRequest sang ExpenseEntity.
	 * - expenseDate, expenseName, amount lấy trực tiếp
	 * - isDeleted luôn là false
	 */
	@Mapping(target = "expenseDate", source = "expenseDate")
	@Mapping(target = "expenseName", source = "expenseName")
	@Mapping(target = "amount", source = "amount")
	@Mapping(target = "isDeleted", constant = "false")
	ExpenseEntity fromRequest(ExpenseRequest request);
}