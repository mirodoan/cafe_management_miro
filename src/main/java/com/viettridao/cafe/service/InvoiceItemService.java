package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.invoice.InvoiceItemListRequest;
import com.viettridao.cafe.dto.response.invoice.InvoiceItemResponse;

import java.util.List;

/**
 * InvoiceItemService
 */
public interface InvoiceItemService {
    /**
     * Thêm danh sách món vào hóa đơn.
     *
     * @param request Danh sách món cần thêm vào hóa đơn.
     * @return Danh sách món đã thêm vào hóa đơn.
     */
    List<InvoiceItemResponse> addItemsToInvoice(InvoiceItemListRequest request);
}