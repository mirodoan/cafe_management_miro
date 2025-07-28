package com.viettridao.cafe.mapper;

import com.viettridao.cafe.dto.response.promotion.PromotionResponse;
import com.viettridao.cafe.model.PromotionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * PromotionMapper
 * Mapper cho thực thể Promotion và DTO PromotionResponse.
 * Chuyển đổi dữ liệu giữa PromotionEntity và PromotionResponse.
 */
@Component
@RequiredArgsConstructor
public class PromotionMapper {
    private final ModelMapper modelMapper;

    /**
     * Chuyển đổi từ PromotionEntity sang PromotionResponse.
     *
     * @param entity Thực thể PromotionEntity cần chuyển đổi.
     * @return Đối tượng PromotionResponse tương ứng.
     */
    public PromotionResponse toPromotionResponse(PromotionEntity entity) {
        PromotionResponse promotionResponse = new PromotionResponse();
        modelMapper.map(entity, promotionResponse);

        return promotionResponse;
    }

    /**
     * Chuyển đổi danh sách PromotionEntity sang danh sách PromotionResponse.
     *
     * @param entities Danh sách thực thể PromotionEntity cần chuyển đổi.
     * @return Danh sách đối tượng PromotionResponse tương ứng.
     */
    public List<PromotionResponse> toPromotionResponseList(List<PromotionEntity> entities) {
        return entities.stream().map(this::toPromotionResponse).toList();
    }
}