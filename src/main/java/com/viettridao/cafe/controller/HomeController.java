package com.viettridao.cafe.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * HomeController
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