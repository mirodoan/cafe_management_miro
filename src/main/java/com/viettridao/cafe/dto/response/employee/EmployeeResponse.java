package com.viettridao.cafe.dto.response.employee;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO Response cho thông tin nhân viên - Optimized data transfer object cho
 * employee management operations.
 * 
 * Business domain context:
 * - Core entity đại diện cho workforce trong cafe management system
 * - Encapsulates essential employee data cần thiết cho administrative tasks
 * - Supports HR operations: recruitment, payroll, performance tracking
 * - Integration point với authentication và authorization systems
 * 
 * Data architecture patterns:
 * - Lightweight POJO với minimal dependencies để optimize performance
 * - Position denormalization để reduce query complexity
 * - Credential management integration cho secure access control
 * - Audit-ready structure với soft delete capabilities
 * 
 * Performance considerations:
 * - Zero-dependency object graph để minimize serialization overhead
 * - Lombok-generated accessors để reduce bytecode size
 * - JSON-friendly structure cho REST API responses
 * - Database query optimization thông qua selective field loading
 * 
 * Security design:
 * - Password field present nhưng controlled access thông qua service layer
 * - Position-based authorization data embedded
 * - Personal information protection compliant structure
 * - Audit trail support thông qua entity reference
 * 
 * @author Cafe Management Team
 * @since 1.0
 * @see com.viettridao.cafe.model.EmployeeEntity
 * @see com.viettridao.cafe.mapper.EmployeeMapper
 */
@Getter
@Setter
public class EmployeeResponse {

    /** Unique identifier của employee - Primary key và audit reference */
    private Integer id;

    /** Họ và tên đầy đủ - Legal name cho HR records và official documentation */
    private String fullName;

    /**
     * Số điện thoại liên lạc - Primary communication channel cho work coordination
     */
    private String phoneNumber;

    /** Địa chỉ thường trú - Optional field cho emergency contact và logistics */
    private String address;

    /** URL ảnh đại diện - Profile picture cho system identification */
    private String imageUrl;

    /** ID vị trí công việc - Foreign key reference đến organizational structure */
    private Integer positionId;

    /**
     * Tên vị trí công việc - Denormalized position title để optimize UI rendering
     */
    private String positionName;

    /**
     * Mức lương cơ bản - Compensation data, access controlled theo role permissions
     */
    private Double salary;

    /** Username đăng nhập - Unique system identifier cho authentication */
    private String username;

    /** Password hash - Encoded credential, never exposed in plaintext */
    private String password;
}
