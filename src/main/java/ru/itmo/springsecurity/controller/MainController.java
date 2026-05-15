package ru.itmo.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.security.Principal;

@Tag(name = "Test Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MainController {

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Доступно всем";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Доступно только пользователям с токеном";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Админская панель";
    }

    @GetMapping("/info")
    public String userData(Principal principal) {
        return principal.getName();
    }
}
