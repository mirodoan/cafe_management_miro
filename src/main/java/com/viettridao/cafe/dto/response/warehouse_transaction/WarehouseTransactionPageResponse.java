package com.viettridao.cafe.dto.response.warehouse_transaction;

import com.viettridao.cafe.dto.response.PageResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DTO for paginated response of warehouse data.
 * Extends the generic PageResponse class.
 */
@Setter
@Getter
public class WarehouseTransactionPageResponse extends PageResponse {
    private List<WarehouseTransactionResponse> transactions;

}
