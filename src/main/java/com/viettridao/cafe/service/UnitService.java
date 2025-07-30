package com.viettridao.cafe.service;

import com.viettridao.cafe.model.UnitEntity;
import com.viettridao.cafe.dto.response.unit.UnitResponse;

import java.util.List;

/**
 * UnitService
 */
public interface UnitService {

    /**
     * Lấy danh sách tất cả các đơn vị.
     *
     * @return Danh sách các thực thể UnitEntity.
     */
    List<UnitEntity> getAllUnits();

    /**
     * Lấy danh sách tất cả các đơn vị dưới dạng UnitResponse cho template.
     *
     * @return Danh sách các UnitResponse.
     */
    List<UnitResponse> getAllUnitResponses();

    /**
     * Lấy thông tin chi tiết của một đơn vị dựa trên ID.
     *
     * @param id ID của đơn vị cần lấy thông tin.
     * @return Thực thể UnitEntity tương ứng với ID.
     */
    UnitEntity getUnitById(Integer id);
}