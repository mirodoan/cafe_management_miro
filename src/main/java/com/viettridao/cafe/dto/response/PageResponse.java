package com.viettridao.cafe.dto.response;

/**
 * DTO cho việc biểu diễn dữ liệu phân trang.
 * Bao gồm thông tin về số trang, kích thước trang, tổng số trang và tổng số phần tử.
 */
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageResponse {
    private long pageNumber;

    private long pageSize;

    private long totalPages;

    private long totalElements;
}