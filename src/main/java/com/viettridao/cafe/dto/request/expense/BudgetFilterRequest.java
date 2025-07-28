package com.viettridao.cafe.dto.request.expense;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetFilterRequest {
    @NotNull(message = "Từ ngày không được để trống")
	@PastOrPresent(message = "Từ ngày phải là ngày hiện tại hoặc trong quá khứ")
	private LocalDate fromDate;

	@NotNull(message = "Đến ngày không được để trống")
	@PastOrPresent(message = "Đến ngày phải là ngày hiện tại hoặc trong quá khứ")
	private LocalDate toDate;

	private int page = 0;

	private int size = 10;
}
