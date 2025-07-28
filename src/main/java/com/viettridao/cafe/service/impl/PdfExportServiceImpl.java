package com.viettridao.cafe.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.viettridao.cafe.common.ReportType;
import com.viettridao.cafe.dto.response.revenue.RevenueItemResponse;
import com.viettridao.cafe.dto.response.revenue.RevenueResponse;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.service.PdfExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * PdfExportServiceImpl
 *
 * Version 1.0
 *
 * Date: 18-07-2025
 *
 * Copyright
 *
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 *
 * Triển khai Service xuất PDF cho các loại báo cáo/quản lý hóa đơn, chi tiêu, nhân viên, nhập/xuất kho, doanh thu.
 */
@Service
@RequiredArgsConstructor
public class PdfExportServiceImpl implements PdfExportService {

    /**
     * Xuất hóa đơn sang PDF.
     *
     * @param invoice hóa đơn cần xuất PDF.
     * @return mảng byte PDF.
     */
    @Override
    public byte[] exportInvoiceToPdf(InvoiceEntity invoice) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Header
        document.add(new Paragraph("🍽 CAFE MANAGEMENT")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

        document.add(new Paragraph("🔖 HÓA ĐƠN THANH TOÁN").setBold().setFontSize(16).setMarginTop(10));
        document.add(new Paragraph("Mã hóa đơn: " + invoice.getId()));
        document.add(new Paragraph("Ngày tạo: " +
                invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        document.add(new Paragraph("\n"));

        // Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2}))
                .useAllAvailableWidth();

        table.addHeaderCell("Tên món");
        table.addHeaderCell("Số lượng");
        table.addHeaderCell("Đơn giá (VNĐ)");
        table.addHeaderCell("Thành tiền (VNĐ)");

        DecimalFormat df = new DecimalFormat("#,###");

        for (InvoiceDetailEntity item : invoice.getInvoiceDetails()) {
            table.addCell(item.getMenuItem().getItemName());
            table.addCell(String.valueOf(item.getQuantity()));
            table.addCell(df.format(item.getPrice()));
            table.addCell(df.format(item.getQuantity() * item.getPrice()));
        }

        document.add(table);
        document.add(new Paragraph("\nTổng cộng: " + df.format(invoice.getTotalAmount()) + " VNĐ").setBold());
        document.close();

        return out.toByteArray();
    }

    /**
     * Hàm tổng hợp xuất các loại báo cáo PDF.
     *
     * @param data dữ liệu báo cáo.
     * @param type kiểu báo cáo.
     * @return mảng byte PDF.
     */
    public static byte[] generatePdf(List<?> data, ReportType type) {
        return switch (type) {
            case EMPLOYEE_SALARY -> generateEmployeeSalaryReport((List<EmployeeEntity>) data);
            case INVOICE_MONTHLY -> generateInvoiceMonthlyReport((List<InvoiceEntity>) data);
            case REVENUE_SUMMARY -> generateRevenueReport((List<RevenueResponse>) data);
            case EXPENSE_ONLY -> generateExpenseReport((List<ExpenseEntity>) data);
            case IMPORT_ONLY -> generateImportReport((List<ImportEntity>) data);
            case EXPORT_ONLY -> generateExportReport((List<ExportEntity>) data);
            case IMPORT_EXPORT -> generateImportExportReport((Map<String, Object>) data.get(0));
        };
    }

    private static byte[] generateEmployeeSalaryReport(List<EmployeeEntity> employees) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO NHÂN VIÊN & LƯƠNG");

        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 3, 2})).useAllAvailableWidth();
        table.addHeaderCell("Họ tên");
        table.addHeaderCell("Chức vụ");
        table.addHeaderCell("Lương (VNĐ)");
        DecimalFormat df = new DecimalFormat("#,###");

        for (EmployeeEntity e : employees) {
            table.addCell(e.getFullName());
            table.addCell(e.getPosition().getPositionName());
            table.addCell(df.format(e.getPosition().getSalary()));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateInvoiceMonthlyReport(List<InvoiceEntity> invoices) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO HÓA ĐƠN THEO THÁNG");

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 3, 3, 2})).useAllAvailableWidth();
        table.addHeaderCell("Mã HD");
        table.addHeaderCell("Ngày tạo");
        table.addHeaderCell("Trạng thái");
        table.addHeaderCell("Tổng tiền");
        DecimalFormat df = new DecimalFormat("#,###");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (InvoiceEntity i : invoices) {
            table.addCell(String.valueOf(i.getId()));
            table.addCell(i.getCreatedAt().toLocalDate().format(dtf));
            table.addCell(i.getStatus().name());
            table.addCell(df.format(i.getTotalAmount()));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateRevenueReport(List<RevenueResponse> data) {
        RevenueResponse res = data.get(0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO DOANH THU");

        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 3, 3})).useAllAvailableWidth();
        table.addHeaderCell("Ngày");
        table.addHeaderCell("Thu (VNĐ)");
        table.addHeaderCell("Chi (VNĐ)");
        DecimalFormat df = new DecimalFormat("#,###");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (RevenueItemResponse i : res.getSummaries()) {
            table.addCell(i.getDate().format(dtf));
            table.addCell(df.format(Optional.ofNullable(i.getIncome()).orElse(0.0)));
            table.addCell(df.format(Optional.ofNullable(i.getExpense()).orElse(0.0)));
        }
        doc.add(table);
        doc.add(new Paragraph("\nTổng thu: " + df.format(res.getTotalIncome()) + " VNĐ").setBold());
        doc.add(new Paragraph("Tổng chi: " + df.format(res.getTotalExpense()) + " VNĐ").setBold());
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateExpenseReport(List<ExpenseEntity> data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO CHI TIÊU");
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 3, 3})).useAllAvailableWidth();
        table.addHeaderCell("Tên khoản chi");
        table.addHeaderCell("Ngày");
        table.addHeaderCell("Số tiền");
        DecimalFormat df = new DecimalFormat("#,###");

        for (ExpenseEntity e : data) {
            table.addCell(e.getExpenseName());
            table.addCell(e.getExpenseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            table.addCell(df.format(e.getAmount()));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateImportReport(List<ImportEntity> data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO NHẬP HÀNG");
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 3, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("Tên sản phẩm");
        table.addHeaderCell("Ngày nhập");
        table.addHeaderCell("Số lượng");
        table.addHeaderCell("Tổng tiền");
        DecimalFormat df = new DecimalFormat("#,###");

        for (ImportEntity i : data) {
            table.addCell(i.getProduct().getProductName());
            table.addCell(i.getImportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            table.addCell(i.getQuantity().toString());
            table.addCell(df.format(i.getTotalAmount()));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateExportReport(List<ExportEntity> data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO XUẤT HÀNG");
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 3, 2, 2})).useAllAvailableWidth();
        table.addHeaderCell("Tên sản phẩm");
        table.addHeaderCell("Ngày xuất");
        table.addHeaderCell("Số lượng");
        table.addHeaderCell("Tổng tiền");
        DecimalFormat df = new DecimalFormat("#,###");

        for (ExportEntity i : data) {
            table.addCell(i.getProduct().getProductName());
            table.addCell(i.getExportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            table.addCell(i.getQuantity().toString());
            table.addCell(df.format(i.getTotalAmount()));
        }
        doc.add(table);
        doc.close();
        return out.toByteArray();
    }

    private static byte[] generateImportExportReport(Map<String, Object> map) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = createDoc(out, "BÁO CÁO NHẬP - XUẤT HÀNG");

        doc.add(new Paragraph("\n📥 NHẬP HÀNG").setBold());
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph(new String(generateImportReport((List<ImportEntity>) map.get("imports")))));

        doc.add(new AreaBreak());
        doc.add(new Paragraph("📤 XUẤT HÀNG").setBold());
        doc.add(new Paragraph("\n"));
        doc.add(new Paragraph(new String(generateExportReport((List<ExportEntity>) map.get("exports")))));

        doc.close();
        return out.toByteArray();
    }

    /**
     * Khởi tạo Document cơ bản cho PDF xuất báo cáo.
     */
    private static Document createDoc(ByteArrayOutputStream out, String title) {
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("🍽 CAFE MANAGEMENT").setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph(title).setBold().setFontSize(16).setMarginTop(10));
        document.add(new Paragraph("\n"));
        return document;
    }
}