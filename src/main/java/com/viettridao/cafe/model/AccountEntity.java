package com.viettridao.cafe.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


/**
 * AccountEntity
 * Thực thể Account lưu thông tin tài khoản.
 */
@Entity
@Getter
@Setter
@Table(name = "accounts") // taikhoan
public class AccountEntity implements Serializable, UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer id; // Khóa chính của bảng accounts

    @Column(name = "username", unique = true)
    private String username; // Tên đăng nhập của tài khoản

    @Column(name = "password")
    private String password; // Mật khẩu của tài khoản

    @Column(name = "permissions")
    private String permission; // Quyền hạn của tài khoản

    @Column(name = "image_url")
    private String imageUrl; // URL ảnh đại diện của tài khoản

    @Column(name = "is_deleted")
    private Boolean isDeleted; // Trạng thái xóa mềm (soft delete)

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL)
    private EmployeeEntity employee; // Nhân viên liên kết với tài khoản

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<ExpenseEntity> expenses; // Danh sách chi tiêu liên kết với tài khoản

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trả về danh sách quyền hạn của tài khoản dưới dạng Collection
        return List.of(new SimpleGrantedAuthority(this.permission));
    }

    @Override
    public boolean isAccountNonExpired() {
        // Kiểm tra xem tài khoản có hết hạn hay không (luôn trả về true)
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        // Kiểm tra xem tài khoản có bị khóa hay không (luôn trả về true)
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // Kiểm tra xem thông tin xác thực có hết hạn hay không (luôn trả về true)
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        // Kiểm tra xem tài khoản có đang hoạt động hay không (dựa vào trạng thái
        // isDeleted)
        return UserDetails.super.isEnabled();
    }
}