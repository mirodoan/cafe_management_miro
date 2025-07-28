package com.viettridao.cafe.dto.response.promotion;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO for representing promotion-related data.
 * Includes details such as promotion name, start and end dates, discount value,
 * and deletion status.
 */
@Getter
@Setter
public class PromotionResponse {
    private Integer id;

    private String promotionName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Double discountValue;

    @Column(name = "is_deleted")
    private Boolean isDeleted;
}