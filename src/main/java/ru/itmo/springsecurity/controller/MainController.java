package ru.itmo.springsecurity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class MainController {

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Доступно всем";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "Доступно только юзерам с токеном";
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
