package com.viettridao.cafe.service;

import com.viettridao.cafe.common.ReportType;
import com.viettridao.cafe.model.InvoiceEntity;

import java.util.List;

public interface ExcelExportService {
    byte[] exportInvoiceToExcel(InvoiceEntity invoice);

    static byte[] generateExcel(List<?> data, ReportType type) {
        // Implement static methods giống như PdfExportServiceImpl
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
