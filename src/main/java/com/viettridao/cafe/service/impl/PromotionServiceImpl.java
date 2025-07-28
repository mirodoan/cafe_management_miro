package com.viettridao.cafe.service.impl;

import com.viettridao.cafe.dto.request.promotion.CreatePromotionRequest;
import com.viettridao.cafe.dto.request.promotion.UpdatePromotionRequest;
import com.viettridao.cafe.dto.response.promotion.PromotionPageResponse;
import com.viettridao.cafe.mapper.PromotionMapper;
import com.viettridao.cafe.model.PromotionEntity;
import com.viettridao.cafe.repository.PromotionRepository;
import com.viettridao.cafe.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * PromotionServiceImpl
 * Service triển khai các chức năng liên quan đến khuyến mãi (Promotion).
 * Thực hiện các logic nghiệp vụ như lấy danh sách, tìm kiếm, tạo mới, cập nhật, và xóa khuyến mãi.
 * Là tầng trung gian xử lý nghiệp vụ giữa controller và repository.
 */
@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    // Repository quản lý dữ liệu khuyến mãi
    private final PromotionRepository promotionRepository;

    // Mapper chuyển đổi giữa Entity và DTO
    private final PromotionMapper promotionMapper;

    /**
     * Lấy danh sách khuyến mãi còn hiệu lực.
     *
     * @param pageable Đối tượng phân trang.
     * @return Đối tượng PromotionPageResponse chứa thông tin khuyến mãi còn hiệu lực.
     */
    @Override
    public PromotionPageResponse getValidPromotions(Pageable pageable, String keyword) {
        LocalDate now = LocalDate.now();

        // Truy vấn DB lấy promotion còn hiệu lực
        Page<PromotionEntity> pageData = promotionRepository
                .findActivePromotions(
                        now, keyword, pageable);
        // Map Entity -> Response (DTO)
        PromotionPageResponse promotionPageResponse = new PromotionPageResponse();
        promotionPageResponse.setPageNumber(pageData.getNumber());
        promotionPageResponse.setPageSize(pageData.getSize());
        promotionPageResponse.setTotalElements(pageData.getTotalElements());
        promotionPageResponse.setTotalPages(pageData.getTotalPages());
        promotionPageResponse.setPromotions(promotionMapper.toPromotionResponseList(pageData.getContent()));

        // Return custom response
        return promotionPageResponse;
    }

    /**
     * Lấy thông tin chi tiết khuyến mãi theo ID.
     *
     * @param id ID của khuyến mãi cần tìm.
     * @return Đối tượng PromotionEntity tương ứng với ID.
     * @throws RuntimeException nếu không tìm thấy khuyến mãi với ID đã cho.
     */
    @Override
    public PromotionEntity getPromotionById(Integer id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi có id = " + id));
    }

    /**
     * Tạo mới một khuyến mãi.
     *
     * @param request Đối tượng chứa thông tin khuyến mãi cần tạo.
     * @return Đối tượng PromotionEntity đã được lưu vào cơ sở dữ liệu.
     * @throws IllegalArgumentException nếu ngày bắt đầu sau ngày kết thúc.
     */
    @Transactional
    @Override
    public PromotionEntity createPromotion(CreatePromotionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
        PromotionEntity promotion = new PromotionEntity();
        promotion.setPromotionName(request.getPromotionName());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setDiscountValue(request.getDiscountValue());
        promotion.setIsDeleted(false);

        return promotionRepository.save(promotion);
    }

    /**
     * Cập nhật thông tin khuyến mãi.
     *
     * @param request Đối tượng chứa thông tin cần cập nhật của khuyến mãi.
     * @throws IllegalArgumentException nếu ngày bắt đầu sau ngày kết thúc hoặc giá
     *                                  trị giảm không hợp lệ.
     */
    @Transactional
    @Override
    public void updatePromotion(UpdatePromotionRequest request) {
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }
        if (request.getDiscountValue() < 0 || request.getDiscountValue() > 100) {
            throw new IllegalArgumentException("Phần trăm giảm phải lớn hơn 0 và nhỏ hơn 100.");
        }
        PromotionEntity promotion = getPromotionById(request.getId());
        promotion.setPromotionName(request.getPromotionName());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setDiscountValue(request.getDiscountValue());

        promotionRepository.save(promotion);
    }

    /**
     * Xóa mềm một khuyến mãi theo ID.
     *
     * @param id ID của khuyến mãi cần xóa.
     */
    @Transactional
    @Override
    public void deletePromotion(Integer id) {
        PromotionEntity promotion = getPromotionById(id);
        promotion.setIsDeleted(true);
        promotionRepository.save(promotion);
    }
}