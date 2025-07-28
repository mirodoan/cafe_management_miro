package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.ReservationEntity;
import com.viettridao.cafe.model.ReservationKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ReservationRepository
 * Repository thao tác với thực thể ReservationEntity.
 */
@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, ReservationKey> {

    /**
     * Tìm danh sách reservation hiện tại (chưa xóa) theo tableId, lấy các record mới nhất theo phân trang.
     *
     * @param tableId  id của bàn
     * @param pageable thông tin phân trang
     * @return List<ReservationEntity>
     */
    @Query("SELECT r FROM ReservationEntity r WHERE r.table.id = :tableId AND r.isDeleted = false ORDER BY r.reservationDate DESC")
    List<ReservationEntity> findCurrentReservationsByTableId(@Param("tableId") Integer tableId, Pageable pageable);

    /**
     * Tìm reservation gần nhất (chưa xóa) theo tableId.
     *
     * @param tableId id của bàn
     * @return Optional<ReservationEntity>
     */
    Optional<ReservationEntity> findTopByTable_IdAndIsDeletedFalseOrderByReservationDateDesc(Integer tableId);

    /**
     * Kiểm tra bàn đã có reservation tại thời điểm xác định chưa (chưa xóa).
     *
     * @param tableId         id của bàn
     * @param reservationDate thời gian đặt bàn
     * @return true nếu tồn tại reservation, ngược lại false
     */
    boolean existsByTableIdAndReservationDateAndIsDeletedFalse(Integer tableId, LocalDateTime reservationDate);

    /**
     * Lấy danh sách reservation theo invoiceId (chưa xóa mềm).
     *
     * @param invoiceId id hóa đơn
     * @return List<ReservationEntity>
     */
    List<ReservationEntity> findByInvoice_IdAndIsDeletedFalse(Integer invoiceId);

    /**
     * Helper method để lấy reservation mới nhất theo tableId.
     *
     * @param tableId id của bàn
     * @return Optional<ReservationEntity>
     */
    default Optional<ReservationEntity> findCurrentReservationByTableId(Integer tableId) {
        List<ReservationEntity> reservations = findCurrentReservationsByTableId(tableId,
                org.springframework.data.domain.PageRequest.of(0, 1));
        return reservations.isEmpty() ? Optional.empty() : Optional.of(reservations.get(0));
    }
}