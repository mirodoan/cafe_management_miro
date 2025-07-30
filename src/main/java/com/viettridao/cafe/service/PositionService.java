package com.viettridao.cafe.service;

import com.viettridao.cafe.model.PositionEntity;

import java.util.List;

/**
 * PositionService
 */
public interface PositionService {

    /**
     * Lấy thông tin chi tiết của một vị trí dựa trên ID.
     *
     * @param id ID của vị trí cần lấy thông tin.
     * @return Thực thể PositionEntity tương ứng với ID.
     */
    PositionEntity getPositionById(Integer id);

    /**
     * Lấy danh sách tất cả các vị trí.
     *
     * @return Danh sách các thực thể PositionEntity.
     */
    List<PositionEntity> getPositions();
}