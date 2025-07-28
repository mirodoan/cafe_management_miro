package com.viettridao.cafe.dto.request.expense;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseRequest {
     @NotBlank(message = "Khoản chi không được để trống")
    private String expenseName;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    private Double amount;

    @NotNull(message = "Ngày chi không được để trống")
    @PastOrPresent(message = "Ngày chi không được lớn hơn hôm nay")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expenseDate;
}
