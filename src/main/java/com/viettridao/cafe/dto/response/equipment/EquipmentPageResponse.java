package com.viettridao.cafe.dto.response.equipment;

import com.viettridao.cafe.dto.response.PageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO cho phản hồi phân trang dữ liệu thiết bị.
 * Kế thừa lớp PageResponse tổng quát.
 */
@Getter
@Setter
public class EquipmentPageResponse extends PageResponse {
    private List<EquipmentResponse> equipments;
}