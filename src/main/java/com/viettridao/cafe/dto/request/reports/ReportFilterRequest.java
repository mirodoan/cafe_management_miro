package com.viettridao.cafe.dto.request.reports;

import com.viettridao.cafe.common.ReportType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ReportFilterRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportType type;
}