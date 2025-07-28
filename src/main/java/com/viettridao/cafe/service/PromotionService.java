package com.viettridao.cafe.service;

import com.viettridao.cafe.dto.request.promotion.CreatePromotionRequest;
import com.viettridao.cafe.dto.request.promotion.UpdatePromotionRequest;
import com.viettridao.cafe.dto.response.promotion.PromotionPageResponse;
import com.viettridao.cafe.model.PromotionEntity;
import org.springframework.data.domain.Pageable;

/**
 * PromotionService
 * Service quản lý các chức năng liên quan đến khuyến mãi (Promotion).
 * Bao gồm: lấy danh sách khuyến mãi, tìm kiếm, tạo mới, cập nhật, và xóa khuyến mãi.
 * Là tầng trung gian xử lý nghiệp vụ giữa controller và repository.
 */
public interface PromotionService {

    /**
     * Lấy danh sách các khuyến mãi còn hiệu lực.
     *
     * @param pageable Đối tượng phân trang.
     * @return Đối tượng PromotionPageResponse chứa danh sách khuyến mãi còn hiệu lực và thông tin phân trang.
     */
    PromotionPageResponse getValidPromotions(Pageable pageable, String keyword);

    /**
     * Lấy thông tin chi tiết của một khuyến mãi dựa trên ID.
     *
     * @param id ID của khuyến mãi cần lấy thông tin.
     * @return Thực thể PromotionEntity tương ứng với ID.
     */
    PromotionEntity getPromotionById(Integer id);

    /**
     * Tạo mới một khuyến mãi.
     *
     * @param request Đối tượng chứa thông tin cần thiết để tạo khuyến mãi mới.
     * @return Thực thể PromotionEntity vừa được tạo.
     */
    PromotionEntity createPromotion(CreatePromotionRequest request);

    /**
     * Cập nhật thông tin khuyến mãi.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật cho khuyến mãi.
     */
    void updatePromotion(UpdatePromotionRequest request);

    /**
     * Xóa mềm một khuyến mãi (đặt isDeleted = true).
     *
     * @param id ID của khuyến mãi cần xóa.
     */
    void deletePromotion(Integer id);
}