package com.viettridao.cafe.dto.response.unit;

/**
 * DTO for representing unit-related data.
 * Includes unit ID, name, and deletion status.
 */
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitResponse {
    private Integer id;

    private String unitName;

    private Boolean isDeleted;
}
