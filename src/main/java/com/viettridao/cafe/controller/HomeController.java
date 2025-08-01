package com.viettridao.cafe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HomeController
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    /**
     * Hiển thị trang chủ/dashboard của ứng dụng.
     *
     * @return template layout chính của ứng dụng
     */
    @GetMapping
    public String home() {
        return "layout";
    }
}