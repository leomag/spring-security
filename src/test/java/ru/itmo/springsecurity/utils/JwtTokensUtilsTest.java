package ru.itmo.springsecurity.utils;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokensUtilsTest {

    private static final String SECRET = "testSecretKeyThatIsLongEnoughForHs256Algorithm";

    private JwtTokensUtils jwtTokensUtils;

    @BeforeEach
    void setUp() {
        jwtTokensUtils = new JwtTokensUtils();
        ReflectionTestUtils.setField(jwtTokensUtils, "secret", SECRET);
        ReflectionTestUtils.setField(jwtTokensUtils, "jwtLifetime", Duration.ofHours(1));
    }

    @Test
    void generateToken_andGetUsername_roundTrip() {
        var userDetails = new User(
                "alice",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));

        var token = jwtTokensUtils.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtTokensUtils.getUsername(token)).isEqualTo("alice");
    }

    @Test
    void getRoles_returnsRolesFromToken() {
        var userDetails = new User(
                "alice",
                "password",
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER")));

        var token = jwtTokensUtils.generateToken(userDetails);

        assertThat(jwtTokensUtils.getRoles(token)).containsExactly("ROLE_USER");
    }

    @Test
    void getRoles_returnsEmptyListWhenClaimMissing() {
        var userDetails = new User("bob", "password", List.of());

        var token = jwtTokensUtils.generateToken(userDetails);

        assertThat(jwtTokensUtils.getRoles(token)).isEmpty();
    }

    @Test
    void getUsername_throwsOnInvalidToken() {
        assertThatThrownBy(() -> jwtTokensUtils.getUsername("not-a-jwt"))
                .isInstanceOf(JwtException.class);
    }
}
