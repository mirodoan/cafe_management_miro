package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.product.CreateProductRequest;
import com.viettridao.cafe.dto.request.product.UpdateProductRequest;
import com.viettridao.cafe.dto.response.product.ProductPageResponse;
import com.viettridao.cafe.dto.response.product.ProductResponse;
import com.viettridao.cafe.mapper.ProductMapper;
import com.viettridao.cafe.model.ProductEntity;
import com.viettridao.cafe.model.UnitEntity;
import com.viettridao.cafe.repository.ProductRepository;
import com.viettridao.cafe.repository.UnitRepository;
import com.viettridao.cafe.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ProductServiceImpl
 * <p>
 * Version 1.0
 * <p>
 * Date: 18-07-2025
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * -------------------------------------------------------
 * 18-07-2025   mirodoan    Create
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UnitRepository unitRepository;
    private final ProductMapper productMapper;

    /**
     * Lấy danh sách sản phẩm có tìm kiếm và phân trang.
     *
     * @param keyword Từ khóa tìm kiếm theo tên sản phẩm (có thể null hoặc rỗng để lấy tất cả)
     * @param page    Số trang (bắt đầu từ 0)
     * @param size    Số lượng phần tử mỗi trang
     * @return ProductPageResponse chứa danh sách sản phẩm và thông tin phân trang
     */
    @Override
    public ProductPageResponse getAllProducts(String keyword, int page, int size) {
        Page<ProductEntity> productPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            productPage = productRepository.getAllProductBySearch(keyword, PageRequest.of(page, size));
        } else {
            productPage = productRepository.getAllProducts(PageRequest.of(page, size));
        }
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(productPage.getNumber());
        response.setPageSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setProducts(productMapper.toListProductResponse(productPage.getContent()));
        return response;
    }

    /**
     * Lấy danh sách sản phẩm chưa bị xóa mềm.
     *
     * @return danh sách sản phẩm cho frontend (dropdown...)
     */
    @Override
    public List<ProductResponse> getAllProducts() {
        List<ProductEntity> entities = productRepository.findAllByIsDeletedFalse();
        return productMapper.toListProductResponse(entities);
    }

    /**
     * Kiểm tra sự tồn tại của sản phẩm theo tên (chỉ kiểm tra sản phẩm chưa bị xóa mềm).
     *
     * @param productName Tên sản phẩm cần kiểm tra
     * @return true nếu tồn tại, false nếu không
     */
    @Override
    public boolean existsByProductNameAndIsDeletedFalse(String productName) {
        return productRepository.existsByProductNameAndIsDeletedFalse(productName);
    }

    /**
     * Kiểm tra trùng tên sản phẩm, loại trừ chính nó (không tính sản phẩm đang cập nhật).
     *
     * @param productName tên sản phẩm
     * @param productId   id sản phẩm cần loại trừ
     * @return true nếu đã tồn tại tên sản phẩm khác với id này
     */
    @Override
    public boolean existsByProductNameAndIsDeletedFalseExceptId(String productName, Integer productId) {
        return productRepository.existsByProductNameAndIsDeletedFalseAndIdNot(productName, productId);
    }

    /**
     * Tạo mới một sản phẩm.
     *
     * @param request Thông tin sản phẩm cần tạo
     * @return ProductEntity vừa được lưu vào database
     * @throws IllegalArgumentException nếu đơn vị tính không tồn tại
     */
    @Override
    @Transactional
    public ProductEntity createProduct(CreateProductRequest request) {
        ProductEntity product = new ProductEntity();
        product.setProductName(request.getProductName());
        product.setQuantity(0);
        product.setIsDeleted(false);
        UnitEntity unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new IllegalArgumentException("Đơn vị tính không tồn tại"));
        product.setUnit(unit);
        return productRepository.save(product);
    }

    /**
     * Xóa mềm một sản phẩm (chỉ đánh dấu isDeleted=true).
     *
     * @param id Id sản phẩm cần xóa
     * @throws IllegalArgumentException nếu không tìm thấy sản phẩm
     */
    @Override
    @Transactional
    public void deleteProduct(Integer id) {
        ProductEntity product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        product.setIsDeleted(true);
        productRepository.save(product);
    }

    /**
     * Cập nhật tên sản phẩm.
     *
     * @param request Thông tin cập nhật (id và tên mới)
     * @throws IllegalArgumentException nếu không tìm thấy sản phẩm
     */
    @Override
    @Transactional
    public void updateProduct(UpdateProductRequest request) {
        ProductEntity product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        product.setProductName(request.getProductName());
        productRepository.save(product);
    }

    /**
     * Lấy chi tiết một sản phẩm theo id.
     *
     * @param id Id sản phẩm
     * @return ProductEntity nếu tìm thấy
     * @throws IllegalArgumentException nếu không tìm thấy sản phẩm
     */
    @Override
    public ProductEntity getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }
}