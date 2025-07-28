package com.viettridao.cafe.dto.response.revenue;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RevenueResponse {
    private List<RevenueItemResponse> summaries;
    private Double totalIncome;
    private Double totalExpense;
}