package com.viettridao.cafe.dto.response.expense;

import java.text.DecimalFormat;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponse1 {
    private LocalDateTime date;

    private Double income;
    private Double expense;

    public String getFormattedIncome() {
        if (income == null)
            return "";
        return formatCurrency(income);
    }

    public String getFormattedExpense() {
        if (expense == null)
            return "";
        return formatCurrency(expense);
    }

    private String formatCurrency(Double value) {
        if (value % 1 == 0) {
            return new DecimalFormat("#,###").format(value);
        } else {
            return new DecimalFormat("#,###.00").format(value);
        }
    }
}
