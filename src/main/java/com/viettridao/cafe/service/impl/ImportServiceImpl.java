package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.imports.CreateImportRequest;
import com.viettridao.cafe.dto.request.imports.UpdateImportRequest;
import com.viettridao.cafe.mapper.ImportMapper;
import com.viettridao.cafe.model.EmployeeEntity;
import com.viettridao.cafe.model.ImportEntity;
import com.viettridao.cafe.model.ProductEntity;
import com.viettridao.cafe.repository.EmployeeRepository;
import com.viettridao.cafe.repository.ImportRepository;
import com.viettridao.cafe.repository.ProductRepository;
import com.viettridao.cafe.service.ImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ImportServiceImpl
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
 * Triển khai các phương thức xử lý logic liên quan đến đơn nhập hàng (Import).
 * Xử lý nhập kho, cập nhật số lượng sản phẩm, tính tổng tiền nhập, và soft delete.
 */
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {

    private final ImportRepository importRepository;
    private final ImportMapper importMapper;
    private final EmployeeRepository employeeRepository;
    private final ProductRepository productRepository;

    /**
     * Tạo mới một đơn nhập hàng, cập nhật số lượng kho và lưu thông tin nhập.
     *
     * @param request thông tin nhập kho cần tạo.
     */
    @Override
    @Transactional
    public void createImport(CreateImportRequest request) {
        // Validate và lấy liên kết từ DB
        EmployeeEntity employee = findEmployeeOrThrow(request.getEmployeeId());
        ProductEntity product = findProductOrThrow(request.getProductId());

        // Cập nhật số lượng sản phẩm trong kho (NHẬP = TĂNG số lượng)
        Integer currentQuantity = product.getQuantity() != null ? product.getQuantity() : 0;
        product.setQuantity(currentQuantity + request.getQuantity());
        productRepository.save(product);

        // Mapping từ DTO → Entity và set các liên kết
        ImportEntity importEntity = importMapper.toEntity(request);
        importEntity.setEmployee(employee);
        importEntity.setProduct(product);

        // Tính toán tổng tiền tự động từ backend
        Double calculatedTotal = request.getUnitImportPrice() * request.getQuantity();
        importEntity.setTotalAmount(calculatedTotal);

        importRepository.save(importEntity);
    }

    /**
     * Lấy thông tin đơn nhập hàng để cập nhật theo id.
     *
     * @param id id đơn nhập cần lấy.
     * @return UpdateImportRequest thông tin cập nhật đơn nhập hàng.
     */
    @Override
    public UpdateImportRequest getUpdateForm(Integer id) {
        ImportEntity importEntity = findImportOrThrow(id);
        return importMapper.toUpdateRequest(importEntity);
    }

    /**
     * Cập nhật thông tin đơn nhập hàng.
     *
     * @param id id đơn nhập cần cập nhật.
     * @param request thông tin cập nhật đơn nhập hàng.
     */
    @Override
    @Transactional
    public void updateImport(Integer id, UpdateImportRequest request) {
        ImportEntity importEntity = findImportOrThrow(id);

        // Cập nhật các trường bình thường từ DTO
        importMapper.updateEntityFromRequest(request, importEntity);

        // Validate và set lại liên kết
        importEntity.setEmployee(findEmployeeOrThrow(request.getEmployeeId()));
        importEntity.setProduct(findProductOrThrow(request.getProductId()));

        importRepository.save(importEntity);
    }

    /**
     * Xóa mềm đơn nhập hàng (đặt isDeleted = true).
     *
     * @param id id đơn nhập cần xóa.
     */
    @Override
    @Transactional
    public void deleteImport(Integer id) {
        ImportEntity importEntity = findImportOrThrow(id);
        importEntity.setIsDeleted(true); // Soft delete
        importRepository.save(importEntity);
    }

    // ===== PRIVATE METHODS =====

    /**
     * Tìm đơn nhập hàng theo id hoặc ném lỗi nếu không tồn tại.
     */
    private ImportEntity findImportOrThrow(Integer id) {
        return importRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn nhập"));
    }

    /**
     * Tìm nhân viên theo id hoặc ném lỗi nếu không tồn tại.
     */
    private EmployeeEntity findEmployeeOrThrow(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nhân viên không tồn tại!"));
    }

    /**
     * Tìm sản phẩm theo id hoặc ném lỗi nếu không tồn tại.
     */
    private ProductEntity findProductOrThrow(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại!"));
    }
}