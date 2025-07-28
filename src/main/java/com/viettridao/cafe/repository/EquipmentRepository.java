package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.EquipmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * EquipmentRepository
 * Repository thao tác với thực thể EquipmentEntity.
 */
@Repository
public interface EquipmentRepository extends JpaRepository<EquipmentEntity, Integer> {

    /**
     * Lấy danh sách tất cả thiết bị không bị xóa mềm.
     *
     * @return List<EquipmentEntity>
     */
    @Query("select e from EquipmentEntity e where e.isDeleted = false")
    List<EquipmentEntity> getAllEquipments();

    /**
     * Lấy danh sách thiết bị không bị xóa mềm theo phân trang.
     *
     * @param pageable phân trang
     * @return Page<EquipmentEntity>
     */
    @Query("select e from EquipmentEntity e where e.isDeleted = false")
    Page<EquipmentEntity> getAllEquipmentsByPage(Pageable pageable);

    /**
     * Tìm thiết bị có ngày mua giữa from và to (không bị xóa mềm).
     *
     * @param from ngày bắt đầu
     * @param to   ngày kết thúc
     * @return List<EquipmentEntity>
     */
    @Query("SELECT e FROM EquipmentEntity e WHERE e.purchaseDate BETWEEN :from AND :to AND e.isDeleted = false")
    List<EquipmentEntity> findEquipmentsBetweenDates(@Param("from") LocalDate from, @Param("to") LocalDate to);

    /**
     * Tìm thiết bị có ngày mua giữa start và end (không bị xóa mềm).
     *
     * @param start ngày bắt đầu
     * @param end   ngày kết thúc
     * @return List<EquipmentEntity>
     */
    List<EquipmentEntity> findByPurchaseDateBetweenAndIsDeletedFalse(LocalDate start, LocalDate end);
}