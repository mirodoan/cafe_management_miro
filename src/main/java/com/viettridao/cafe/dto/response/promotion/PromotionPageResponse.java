package com.viettridao.cafe.dto.response.promotion;

import com.viettridao.cafe.dto.response.PageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * PromotionPageResponse
 * DTO cho phản hồi phân trang dữ liệu khuyến mãi.
 * Kế thừa lớp PageResponse tổng quát.
 */
@Getter
@Setter
public class PromotionPageResponse extends PageResponse {
    private List<PromotionResponse> promotions;
}