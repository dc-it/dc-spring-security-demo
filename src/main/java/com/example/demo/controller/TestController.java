package com.example.demo.controller;

import com.example.demo.common.api.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

/**
 * 测试前端控制器
 *
 * @author duchao
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RolesAllowed("USER")
    @GetMapping("/user")
    public Result<String> testWithUserRole() {
        return Result.success("Hello Spring Security !（USER）");
    }

    @RolesAllowed("ADMIN")
    @GetMapping("/admin")
    public Result<String> testWithoutAdminRole() {
        return Result.success("Hello Spring Security !(ADMIN)");
    }
}
