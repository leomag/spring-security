package ru.itmo.springsecurity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itmo.springsecurity.entity.Role;
import ru.itmo.springsecurity.repository.RoleRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void getUserRoleReturnsRoleUser() {
        var role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        var result = roleService.getUserRole();

        assertThat(result).isEqualTo(role);
    }

    @Test
    void getUserRoleThrowsWhenRoleMissing() {
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        assertThatThrownBy(roleService::getUserRole).isInstanceOf(NoSuchElementException.class);
    }
}
