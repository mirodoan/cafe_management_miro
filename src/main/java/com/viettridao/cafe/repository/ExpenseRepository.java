package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ExpenseRepository
 * Repository thao tác với thực thể ExpenseEntity.
 */
@Repository
public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Integer> {

    /**
     * Lấy danh sách các khoản chi trong khoảng thời gian from - to (không bị xóa mềm).
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @return List<ExpenseEntity>
     */
    @Query("SELECT e FROM ExpenseEntity e WHERE e.expenseDate BETWEEN :from AND :to AND e.isDeleted = false")
    List<ExpenseEntity> findExpensesBetweenDates(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Tính tổng số tiền chi trong ngày (không bị xóa mềm).
     *
     * @param date ngày chi
     * @return tổng số tiền chi (Double)
     */
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.expenseDate = :date AND e.isDeleted = false")
    Double sumAmountByDate(@Param("date") LocalDate date);

    /**
     * Tính tổng số tiền chi trong khoảng thời gian from - to (không bị xóa mềm).
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @return tổng số tiền chi (Double)
     */
    @Query("SELECT SUM(e.amount) FROM ExpenseEntity e WHERE e.expenseDate BETWEEN :from AND :to AND e.isDeleted = false")
    Double sumAmountBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Lấy danh sách các khoản chi trong khoảng thời gian start - end (không bị xóa mềm).
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return List<ExpenseEntity>
     */
    List<ExpenseEntity> findByExpenseDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end);

}