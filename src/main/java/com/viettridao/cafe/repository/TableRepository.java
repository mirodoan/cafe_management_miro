package com.viettridao.cafe.repository;

import com.viettridao.cafe.common.TableStatus;
import com.viettridao.cafe.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TableRepository
 * Repository thao tác với thực thể TableEntity.
 */
@Repository
public interface TableRepository extends JpaRepository<TableEntity, Integer> {

    /**
     * Lấy danh sách bàn theo trạng thái.
     *
     * @param status trạng thái bàn
     * @return List<TableEntity>
     */
    List<TableEntity> findByStatus(TableStatus status);

    /**
     * Tìm bàn theo id hóa đơn của reservation.
     *
     * @param invoiceId id hóa đơn
     * @return TableEntity
     */
    TableEntity findByReservations_Invoice_Id(Integer invoiceId);
}