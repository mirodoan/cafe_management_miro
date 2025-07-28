package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.PromotionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * PromotionRepository
 * Repository cho thực thể PromotionEntity.
 * Chịu trách nhiệm truy vấn dữ liệu liên quan đến khuyến mãi (Promotion) từ cơ sở dữ liệu.
 */
@Repository
public interface PromotionRepository extends JpaRepository<PromotionEntity, Integer> {

    /**
     * Lấy tất cả các khuyến mãi chưa bị xóa.
     *
     * @param pageable Đối tượng phân trang.
     * @return Trang kết quả chứa danh sách khuyến mãi chưa bị xóa.
     */
    Page<PromotionEntity> findByIsDeletedFalse(Pageable pageable);

    /**
     * Lấy danh sách các khuyến mãi chưa bị xóa và còn hiệu lực (đang active), có thể tìm theo tên
     *
     * @param now      Ngày hiện tại để kiểm tra hiệu lực khuyến mãi.
     * @param pageable Đối tượng phân trang.
     * @return Trang kết quả chứa danh sách khuyến mãi đang active.
     */
    @Query("SELECT p FROM PromotionEntity p WHERE p.isDeleted = false " +
            "AND p.startDate <= :now AND p.endDate >= :now " +
            "AND (:keyword IS NULL OR :keyword = '' OR lower(p.promotionName) LIKE lower(CONCAT('%', :keyword, '%')))")
    Page<PromotionEntity> findActivePromotions(@Param("now") LocalDate now,
                                               @Param("keyword") String keyword, Pageable pageable);
}