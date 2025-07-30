package com.viettridao.cafe.service;

import org.springframework.data.domain.Page;
import com.viettridao.cafe.dto.request.expense.BudgetFilterRequest;
import com.viettridao.cafe.dto.request.expense.ExpenseRequest;
import com.viettridao.cafe.dto.response.expense.BudgetResponse;

/**
 * BudgetService
 */
public interface BudgetService {
    /**
     * Lấy danh sách ngân sách và chi phí theo bộ lọc.
     *
     * @param request Bộ lọc ngân sách/chi phí.
     * @return Trang dữ liệu ngân sách/chi phí.
     */
    Page<BudgetResponse> getBudgetView(BudgetFilterRequest request);

    /**
     * Thêm chi phí mới vào ngân sách.
     *
     * @param request  Thông tin chi phí.
     * @param username Tên đăng nhập người thực hiện.
     */
    void addExpense(ExpenseRequest request, String username);
}