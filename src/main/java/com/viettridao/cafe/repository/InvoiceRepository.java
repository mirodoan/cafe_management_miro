package com.viettridao.cafe.repository;

import com.viettridao.cafe.common.InvoiceStatus;
import com.viettridao.cafe.model.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * InvoiceRepository
 * Repository thao tác với thực thể InvoiceEntity.
 */
@Repository
public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Integer> {

    /**
     * Lấy danh sách hóa đơn theo trạng thái và khoảng thời gian tạo.
     *
     * @param status trạng thái hóa đơn
     * @param from   thời gian bắt đầu
     * @param to     thời gian kết thúc
     * @return List<InvoiceEntity>
     */
    List<InvoiceEntity> findByStatusAndCreatedAtBetween(InvoiceStatus status, LocalDateTime from, LocalDateTime to);

    /**
     * Lấy hóa đơn gần nhất theo bàn, trạng thái và chưa bị xóa mềm.
     *
     * @param tableId id bàn
     * @param status  trạng thái hóa đơn
     * @return InvoiceEntity
     */
    InvoiceEntity findTopByReservations_Table_IdAndStatusAndIsDeletedFalseOrderByCreatedAtDesc(Integer tableId, InvoiceStatus status);

    /**
     * Tính tổng tiền các hóa đơn đã thanh toán trong ngày.
     *
     * @param date ngày cần tính
     * @return tổng tiền các hóa đơn đã thanh toán
     */
    @Query("SELECT SUM(i.totalAmount) FROM InvoiceEntity i WHERE DATE(i.createdAt) = :date AND i.status = 'PAID' AND i.isDeleted = false")
    Double sumTotalAmountByDate(@Param("date") LocalDate date);

    /**
     * Tính tổng tiền các hóa đơn đã thanh toán trong khoảng thời gian.
     *
     * @param from thời gian bắt đầu
     * @param to   thời gian kết thúc
     * @return tổng tiền các hóa đơn đã thanh toán
     */
    @Query("SELECT SUM(i.totalAmount) FROM InvoiceEntity i WHERE i.createdAt BETWEEN :from AND :to AND i.status = 'PAID' AND i.isDeleted = false")
    Double sumTotalAmountBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Lấy danh sách hóa đơn chưa bị xóa mềm theo khoảng thời gian tạo.
     *
     * @param start thời gian bắt đầu
     * @param end   thời gian kết thúc
     * @return List<InvoiceEntity>
     */
    List<InvoiceEntity> findByCreatedAtBetweenAndIsDeletedFalse(LocalDateTime start, LocalDateTime end);

    /**
     * Lấy danh sách hóa đơn chưa bị xóa mềm và theo trạng thái trong khoảng thời gian tạo.
     *
     * @param start  thời gian bắt đầu
     * @param end    thời gian kết thúc
     * @param status trạng thái hóa đơn
     * @return List<InvoiceEntity>
     */
    List<InvoiceEntity> findByCreatedAtBetweenAndIsDeletedFalseAndStatusEquals(LocalDateTime start, LocalDateTime end, InvoiceStatus status);

}