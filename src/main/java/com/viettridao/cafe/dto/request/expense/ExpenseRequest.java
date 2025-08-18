package com.viettridao.cafe.dto.request.expense;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseRequest {
    @NotBlank(message = "Khoản chi không được để trống")
    @Size(min = 5, message = "Khoản chi tối thiểu 5 ký tự")
    @Size(max = 20, message = "Khoản chi tối đa 20 ký tự")
    private String expenseName;

    @NotNull(message = "Số tiền không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Số tiền phải lớn hơn 0")
    @DecimalMax(value = "999999999.0", message = "Số tiền phải nhỏ hơn 999.999.999")
    private Double amount;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @NotNull(message = "Ngày chi không được để trống")
    @PastOrPresent(message = "Ngày chi không được lớn hơn hôm nay")
    private LocalDate expenseDate;
}
