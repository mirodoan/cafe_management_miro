package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.InvoiceDetailEntity;
import com.viettridao.cafe.model.InvoiceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * InvoiceDetailRepository
 * Repository thao tác với chi tiết hóa đơn (InvoiceDetailEntity).
 */
@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetailEntity, InvoiceKey> {

    /**
     * Lấy danh sách chi tiết hóa đơn chưa bị xóa mềm theo id hóa đơn.
     *
     * @param invoiceId id của hóa đơn
     * @return List<InvoiceDetailEntity>
     */
    List<InvoiceDetailEntity> findAllByInvoice_IdAndIsDeletedFalse(Integer invoiceId);


    /**
     * Tìm chi tiết hóa đơn theo id và chưa bị xóa mềm.
     *
     * @param id InvoiceKey của chi tiết hóa đơn
     * @return Optional<InvoiceDetailEntity>
     */
    Optional<InvoiceDetailEntity> findByIdAndIsDeletedFalse(InvoiceKey id);

}