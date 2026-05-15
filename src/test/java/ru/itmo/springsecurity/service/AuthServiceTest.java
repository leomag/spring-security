package ru.itmo.springsecurity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import ru.itmo.springsecurity.dto.JwtRequest;
import ru.itmo.springsecurity.dto.RegistrationUserDto;
import ru.itmo.springsecurity.entity.UserDto;
import ru.itmo.springsecurity.exception.AppError;
import ru.itmo.springsecurity.utils.JwtTokensUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtTokensUtils jwtTokensUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    void createAuthToken_returnsJwtWhenCredentialsValid() {
        var request = new JwtRequest("alice", "secret");
        var userDetails = new User("alice", "hash", List.of());
        when(userService.loadUserByUsername("alice")).thenReturn(userDetails);
        when(jwtTokensUtils.generateToken(userDetails)).thenReturn("jwt-token");

        var response = authService.createAuthToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isInstanceOf(ru.itmo.springsecurity.dto.JwtResponse.class);
        assertThat(((ru.itmo.springsecurity.dto.JwtResponse) response.getBody()).token()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void createAuthToken_returnsUnauthorizedWhenCredentialsInvalid() {
        var request = new JwtRequest("alice", "wrong");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad credentials"));

        var response = authService.createAuthToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        var body = (AppError) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("Not valid login or password");
        verify(userService, never()).loadUserByUsername(any());
    }

    @Test
    void createNewUser_returnsBadRequestWhenPasswordsDoNotMatch() {
        var dto = new RegistrationUserDto("alice", "pass1", "pass2", "alice@example.com");

        var response = authService.createNewUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((AppError) response.getBody()).getMessage()).isEqualTo("Passwords not match");
        verify(userService, never()).getNewUser(any());
    }

    @Test
    void createNewUser_returnsBadRequestWhenUsernameExists() {
        var dto = new RegistrationUserDto("alice", "pass", "pass", "alice@example.com");
        when(userService.findByUsername("alice")).thenReturn(Optional.of(new ru.itmo.springsecurity.entity.User()));

        var response = authService.createNewUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(((AppError) response.getBody()).getMessage()).isEqualTo("User with name exists");
        verify(userService, never()).getNewUser(any());
    }

    @Test
    void createNewUser_returnsUserDtoWhenRegistrationSuccessful() {
        var dto = new RegistrationUserDto("alice", "pass", "pass", "alice@example.com");
        when(userService.findByUsername("alice")).thenReturn(Optional.empty());
        var savedUser = new ru.itmo.springsecurity.entity.User();
        savedUser.setId(1L);
        savedUser.setUsername("alice");
        savedUser.setEmail("alice@example.com");
        when(userService.getNewUser(dto)).thenReturn(savedUser);

        var response = authService.createNewUser(dto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        var body = (UserDto) response.getBody();
        assertThat(body.id()).isEqualTo(1L);
        assertThat(body.username()).isEqualTo("alice");
        assertThat(body.email()).isEqualTo("alice");
    }
}
