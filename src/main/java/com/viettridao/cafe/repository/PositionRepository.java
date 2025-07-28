package com.viettridao.cafe.repository;

import com.viettridao.cafe.model.PositionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PositionRepository
 * Repository cho thực thể PositionEntity.
 * Chịu trách nhiệm truy vấn dữ liệu liên quan đến vị trí (Position) từ cơ sở dữ liệu.
 */
@Repository
public interface PositionRepository extends JpaRepository<PositionEntity, Integer> {

    /**
     * Lấy danh sách tất cả các vị trí chưa bị xóa.
     *
     * @return Danh sách các thực thể PositionEntity chưa bị xóa.
     */
    @Query("select p from PositionEntity p where p.isDeleted = false")
    List<PositionEntity> getAllPositions();
}