package com.viettridao.cafe.dto.request.expense;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class BudgetFilterRequest {
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Từ ngày không được để trống")
    @PastOrPresent(message = "Từ ngày phải là ngày hiện tại hoặc trong quá khứ")
    private LocalDate fromDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Đến ngày không được để trống")
    @PastOrPresent(message = "Đến ngày phải là ngày hiện tại hoặc trong quá khứ")
    private LocalDate toDate;

    private int page = 0;

    private int size = 10;
}
