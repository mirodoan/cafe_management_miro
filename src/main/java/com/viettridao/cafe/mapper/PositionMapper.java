package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.position.PositionResponse;
import com.viettridao.cafe.model.PositionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PositionMapper
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
 * Mapper cho thực thể Position và DTO PositionResponse.
 * Chuyển đổi dữ liệu giữa PositionEntity và PositionResponse.
 */
@Component
@RequiredArgsConstructor
public class PositionMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển đổi từ PositionEntity sang PositionResponse.
     *
     * @param entity Thực thể PositionEntity cần chuyển đổi.
     * @return Đối tượng PositionResponse tương ứng.
     */
    public PositionResponse toPositionResponse(PositionEntity entity) {
        return modelMapper.map(entity, PositionResponse.class);
    }

    /**
     * Chuyển đổi danh sách PositionEntity sang danh sách PositionResponse.
     *
     * @param entities Danh sách thực thể PositionEntity cần chuyển đổi.
     * @return Danh sách đối tượng PositionResponse tương ứng.
     */
    public List<PositionResponse> toListPositionResponse(List<PositionEntity> entities) {
        return entities.stream().map(this::toPositionResponse).toList();
    }
}