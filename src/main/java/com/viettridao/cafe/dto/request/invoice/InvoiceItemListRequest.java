package com.viettridao.cafe.dto.request.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class InvoiceItemListRequest {
    @NotEmpty(message = "Danh sách món không được để trống")
    @Valid
    private List<InvoiceItemRequest> items;
}
