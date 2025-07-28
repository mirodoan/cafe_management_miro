package com.viettridao.cafe.dto.response.position;

/**
 * DTO cho việc biểu diễn dữ liệu liên quan đến vị trí công việc.
 * Bao gồm thông tin về tên vị trí, mức lương và trạng thái xóa.
 */
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionResponse {
    private Integer id;
    private String positionName;
    private Double salary;
    private Boolean isDeleted;
}
