package com.viettridao.cafe.service;

import com.viettridao.cafe.model.InvoiceEntity;

/**
 * PdfExportService
 * Interface định nghĩa dịch vụ xuất hóa đơn ra file PDF cho hệ thống quán cafe.
 * - Dùng cho các chức năng in hoặc tải hóa đơn dưới dạng PDF.
 */
public interface PdfExportService {
    /**
     * Xuất hóa đơn ra file PDF.
     *
     * @param invoice Hóa đơn cần xuất PDF.
     * @return Mảng byte của file PDF được sinh ra.
     */
    byte[] exportInvoiceToPdf(InvoiceEntity invoice);
}