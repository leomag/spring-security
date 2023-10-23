package ru.itmo.springsecurity.entity;

import lombok.AccessLevel;
import lombok.Setter;
public record UserDto(Long id, String username, String email) {
}
