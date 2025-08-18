package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.common.ReportType;
import com.viettridao.cafe.dto.response.revenue.RevenueItemResponse;
import com.viettridao.cafe.dto.response.revenue.RevenueResponse;
import com.viettridao.cafe.model.*;
import com.viettridao.cafe.service.ExcelExportService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ExcelExportServiceImpl
 * Triển khai Service xuất Excel cho các loại báo cáo/quản lý hóa đơn, chi tiêu, nhân viên, nhập/xuất kho, doanh thu.
 */
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] exportInvoiceToExcel(InvoiceEntity invoice) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hóa Đơn");
            int rowIdx = 0;

            // Header
            Row header1 = sheet.createRow(rowIdx++);
            header1.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));

            Row header2 = sheet.createRow(rowIdx++);
            header2.createCell(0).setCellValue("🔖 HÓA ĐƠN THANH TOÁN");

            Row infoRow = sheet.createRow(rowIdx++);
            infoRow.createCell(0).setCellValue("Mã hóa đơn: " + invoice.getId());
            infoRow.createCell(1).setCellValue("Ngày tạo: " +
                    invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

            rowIdx++; // Blank row

            // Table Header
            Row tableHeader = sheet.createRow(rowIdx++);
            tableHeader.createCell(0).setCellValue("Tên món");
            tableHeader.createCell(1).setCellValue("Số lượng");
            tableHeader.createCell(2).setCellValue("Đơn giá (VND)");
            tableHeader.createCell(3).setCellValue("Thành tiền (VND)");

            DecimalFormat df = new DecimalFormat("#,###");
            for (InvoiceDetailEntity item : invoice.getInvoiceDetails()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getMenuItem().getItemName());
                row.createCell(1).setCellValue(item.getQuantity());
                row.createCell(2).setCellValue(df.format(item.getPrice()));
                row.createCell(3).setCellValue(df.format(item.getQuantity() * item.getPrice()));
            }

            rowIdx++;
            Row totalRow = sheet.createRow(rowIdx);
            totalRow.createCell(0).setCellValue("Tổng cộng: " + df.format(invoice.getTotalAmount()) + " VND");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Export Invoice to Excel failed", e);
        }
    }

    public static byte[] generateExcel(List<?> data, ReportType type) {
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
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Nhân Viên & Lương");
            int rowIdx = 0;

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO NHÂN VIÊN & LƯƠNG");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Họ tên");
            header.createCell(1).setCellValue("Chức vụ");
            header.createCell(2).setCellValue("Lương (VND)");

            DecimalFormat df = new DecimalFormat("#,###");
            for (EmployeeEntity e : employees) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getFullName());
                row.createCell(1).setCellValue(e.getPosition().getPositionName());
                row.createCell(2).setCellValue(df.format(e.getPosition().getSalary()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Employee Salary Report to Excel failed", ex);
        }
    }

    private static byte[] generateInvoiceMonthlyReport(List<InvoiceEntity> invoices) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Hóa Đơn Tháng");
            int rowIdx = 0;

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO HÓA ĐƠN THEO THÁNG");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Mã HD");
            header.createCell(1).setCellValue("Ngày tạo");
            header.createCell(2).setCellValue("Tổng tiền");

            DecimalFormat df = new DecimalFormat("#,###");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (InvoiceEntity i : invoices) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(String.valueOf(i.getId()));
                row.createCell(1).setCellValue(i.getCreatedAt().toLocalDate().format(dtf));
                row.createCell(2).setCellValue(i.getStatus().name());
                row.createCell(3).setCellValue(df.format(i.getTotalAmount()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Invoice Monthly Report to Excel failed", ex);
        }
    }

    private static byte[] generateRevenueReport(List<RevenueResponse> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Doanh Thu");
            int rowIdx = 0;

            RevenueResponse res = data.get(0);

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO DOANH THU");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Ngày");
            header.createCell(1).setCellValue("Thu (VND)");
            header.createCell(2).setCellValue("Chi (VND)");

            DecimalFormat df = new DecimalFormat("#,###");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (RevenueItemResponse i : res.getSummaries()) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i.getDate().format(dtf));
                row.createCell(1).setCellValue(df.format(Optional.ofNullable(i.getIncome()).orElse(0.0)));
                row.createCell(2).setCellValue(df.format(Optional.ofNullable(i.getExpense()).orElse(0.0)));
            }

            rowIdx++;
            Row totalIncomeRow = sheet.createRow(rowIdx++);
            totalIncomeRow.createCell(0).setCellValue("Tổng thu: " + df.format(res.getTotalIncome()) + " VND");
            Row totalExpenseRow = sheet.createRow(rowIdx);
            totalExpenseRow.createCell(0).setCellValue("Tổng chi: " + df.format(res.getTotalExpense()) + " VND");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Revenue Report to Excel failed", ex);
        }
    }

    private static byte[] generateExpenseReport(List<ExpenseEntity> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Chi Tiêu");
            int rowIdx = 0;

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 2));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO CHI TIÊU");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Tên khoản chi");
            header.createCell(1).setCellValue("Ngày");
            header.createCell(2).setCellValue("Số tiền");

            DecimalFormat df = new DecimalFormat("#,###");
            for (ExpenseEntity e : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getExpenseName());
                row.createCell(1).setCellValue(e.getExpenseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(df.format(e.getAmount()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Expense Report to Excel failed", ex);
        }
    }

    private static byte[] generateImportReport(List<ImportEntity> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Nhập Hàng");
            int rowIdx = 0;

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO NHẬP HÀNG");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Tên sản phẩm");
            header.createCell(1).setCellValue("Ngày nhập");
            header.createCell(2).setCellValue("Số lượng");
            header.createCell(3).setCellValue("Tổng tiền");

            DecimalFormat df = new DecimalFormat("#,###");
            for (ImportEntity i : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i.getProduct().getProductName());
                row.createCell(1).setCellValue(i.getImportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(i.getQuantity());
                row.createCell(3).setCellValue(df.format(i.getTotalAmount()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Import Report to Excel failed", ex);
        }
    }

    private static byte[] generateExportReport(List<ExportEntity> data) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Báo Cáo Xuất Hàng");
            int rowIdx = 0;

            Row title = sheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            Row subtitle = sheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("BÁO CÁO XUẤT HÀNG");

            rowIdx++;

            Row header = sheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Tên sản phẩm");
            header.createCell(1).setCellValue("Ngày xuất");
            header.createCell(2).setCellValue("Số lượng");
            header.createCell(3).setCellValue("Tổng tiền");

            DecimalFormat df = new DecimalFormat("#,###");
            for (ExportEntity i : data) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i.getProduct().getProductName());
                row.createCell(1).setCellValue(i.getExportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(i.getQuantity());
                row.createCell(3).setCellValue(df.format(i.getTotalAmount()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Export Report to Excel failed", ex);
        }
    }

    private static byte[] generateImportExportReport(Map<String, Object> map) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Nhập Hàng
            Sheet importSheet = workbook.createSheet("Nhập Hàng");
            int rowIdx = 0;
            Row title = importSheet.createRow(rowIdx++);
            title.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            importSheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            Row subtitle = importSheet.createRow(rowIdx++);
            subtitle.createCell(0).setCellValue("📥 NHẬP HÀNG");

            rowIdx++;
            Row header = importSheet.createRow(rowIdx++);
            header.createCell(0).setCellValue("Tên sản phẩm");
            header.createCell(1).setCellValue("Ngày nhập");
            header.createCell(2).setCellValue("Số lượng");
            header.createCell(3).setCellValue("Tổng tiền");

            List<ImportEntity> imports = (List<ImportEntity>) map.get("imports");
            DecimalFormat df = new DecimalFormat("#,###");
            for (ImportEntity i : imports) {
                Row row = importSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(i.getProduct().getProductName());
                row.createCell(1).setCellValue(i.getImportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(i.getQuantity());
                row.createCell(3).setCellValue(df.format(i.getTotalAmount()));
            }

            // Xuất Hàng
            Sheet exportSheet = workbook.createSheet("Xuất Hàng");
            int exportRowIdx = 0;
            Row expTitle = exportSheet.createRow(exportRowIdx++);
            expTitle.createCell(0).setCellValue("🍽 CAFE MANAGEMENT");
            exportSheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 3));
            Row expSubtitle = exportSheet.createRow(exportRowIdx++);
            expSubtitle.createCell(0).setCellValue("📤 XUẤT HÀNG");

            exportRowIdx++;
            Row expHeader = exportSheet.createRow(exportRowIdx++);
            expHeader.createCell(0).setCellValue("Tên sản phẩm");
            expHeader.createCell(1).setCellValue("Ngày xuất");
            expHeader.createCell(2).setCellValue("Số lượng");
            expHeader.createCell(3).setCellValue("Tổng tiền");

            List<ExportEntity> exports = (List<ExportEntity>) map.get("exports");
            for (ExportEntity i : exports) {
                Row row = exportSheet.createRow(exportRowIdx++);
                row.createCell(0).setCellValue(i.getProduct().getProductName());
                row.createCell(1).setCellValue(i.getExportDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                row.createCell(2).setCellValue(i.getQuantity());
                row.createCell(3).setCellValue(df.format(i.getTotalAmount()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Export Import-Export Report to Excel failed", ex);
        }
    }
}