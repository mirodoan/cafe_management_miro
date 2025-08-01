package com.viettridao.cafe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * AboutController
 */
@Controller
@RequestMapping("/about")
public class AboutController {

    /**
     * Hiển thị trang giới thiệu của ứng dụng.
     *
     * @return đường dẫn tới template about.html trong thư mục abouts/
     */
    @GetMapping("")
    public String showAboutPage() {
        // Trả về view template - Spring sẽ resolve thành abouts/about.html
        return "abouts/about";
    }
}