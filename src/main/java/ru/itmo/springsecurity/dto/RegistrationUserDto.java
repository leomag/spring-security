package ru.itmo.springsecurity.dto;

public record RegistrationUserDto(String username, String password, String confirmPassword, String email) {
}
