package ru.itmo.springsecurity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmo.springsecurity.dto.RegistrationUserDto;
import ru.itmo.springsecurity.entity.Role;
import ru.itmo.springsecurity.entity.User;
import ru.itmo.springsecurity.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        userService.setUserRepository(userRepository);
        userService.setRoleService(roleService);
        userService.setPasswordEncoder(passwordEncoder);
    }

    @Test
    void findByUsername_delegatesToRepository() {
        var user = new User();
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        var result = userService.findByUsername("alice");

        assertThat(result).contains(user);
    }

    @Test
    void loadUserByUsername_returnsUserDetailsWithRoles() {
        var role = new Role();
        role.setName("ROLE_USER");
        var user = new User();
        user.setUsername("alice");
        user.setPassword("encoded");
        user.setRoles(List.of(role));
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        var details = userService.loadUserByUsername("alice");

        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("encoded");
        assertThat(details.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
    }

    @Test
    void loadUserByUsername_throwsWhenUserNotFound() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User 'missing' not found");
    }

    @Test
    void getNewUser_encodesPasswordAndAssignsRole() {
        var dto = new RegistrationUserDto("alice", "plain", "plain", "alice@example.com");
        var role = new Role();
        role.setName("ROLE_USER");
        when(roleService.getUserRole()).thenReturn(role);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setId(42L);
            return saved;
        });

        var user = userService.getNewUser(dto);

        assertThat(user.getId()).isEqualTo(42L);
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getEmail()).isEqualTo("alice@example.com");
        assertThat(user.getPassword()).isEqualTo("encoded");
        assertThat(user.getRoles()).containsExactly(role);
        verify(passwordEncoder).encode("plain");
    }
}
