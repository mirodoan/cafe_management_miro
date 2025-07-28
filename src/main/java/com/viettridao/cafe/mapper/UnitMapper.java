package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.unit.UnitResponse;
import com.viettridao.cafe.model.UnitEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * UnitMapper
 *
 * Version 1.0
 *
 * Date: 19-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 19-07-2025   mirodoan    Create
 *
 * Mapper cho thực thể Unit và DTO UnitResponse.
 * Chuyển đổi dữ liệu giữa UnitEntity và UnitResponse.
 */
@Component
@RequiredArgsConstructor
public class UnitMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển đổi từ UnitEntity sang UnitResponse.
     *
     * @param entity Thực thể UnitEntity cần chuyển đổi.
     * @return Đối tượng UnitResponse tương ứng.
     */
    public UnitResponse toUnitResponse(UnitEntity entity) {
        return modelMapper.map(entity, UnitResponse.class);
    }

    /**
     * Chuyển đổi danh sách UnitEntity sang danh sách UnitResponse.
     *
     * @param entities Danh sách thực thể UnitEntity cần chuyển đổi.
     * @return Danh sách đối tượng UnitResponse tương ứng.
     */
    public List<UnitResponse> toUnitResponseList(List<UnitEntity> entities) {
        return entities.stream().map(this::toUnitResponse).toList();
    }

}