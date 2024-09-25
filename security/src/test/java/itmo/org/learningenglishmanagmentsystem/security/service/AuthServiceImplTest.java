package itmo.org.learningenglishmanagmentsystem.security.service;

import io.jsonwebtoken.Claims;
import itmo.org.learningenglishmanagmentsystem.core.entity.Role;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.core.entity.enums.Roles;
import itmo.org.learningenglishmanagmentsystem.security.dto.UserDTO;
import itmo.org.learningenglishmanagmentsystem.security.entity.jwt.JwtRequest;
import itmo.org.learningenglishmanagmentsystem.security.entity.jwt.JwtResponse;
import itmo.org.learningenglishmanagmentsystem.security.exception.JwtException;
import itmo.org.learningenglishmanagmentsystem.security.mapper.UserMapper;
import itmo.org.learningenglishmanagmentsystem.security.repository.RoleRepositorySecurity;
import itmo.org.learningenglishmanagmentsystem.security.repository.UserRepositorySecurity;
import itmo.org.learningenglishmanagmentsystem.security.securitys.HashingServiceImpl;
import itmo.org.learningenglishmanagmentsystem.security.securitys.JwtProviderImpl;
import itmo.org.learningenglishmanagmentsystem.security.service.impl.AuthServiceImpl;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AuthServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private UserRepositorySecurity userRepositorySecurity;

    @Mock
    private JwtProviderImpl jwtProviderImpl;

    @Mock
    private HashingServiceImpl hashingService;

    @Mock
    private RoleRepositorySecurity roleRepositorySecurity;

    @Mock
    private UserMapper userMapper;
    @Mock
    private final Map<String, String> refreshStorage = new HashMap<>();
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthServiceImpl(userService, userRepositorySecurity, jwtProviderImpl, userMapper, hashingService, roleRepositorySecurity);
    }

    @Test
    void registration_NewUser_ReturnsJwtResponse() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setLogin("testUser");
        userDTO.setPassword("password");
        User user = new User();
        user.setLogin("testUser");
        user.setPassword("password");

        given(userRepositorySecurity.findUserByLogin(userDTO.getLogin())).willReturn(Optional.empty());
        given(roleRepositorySecurity.findByName(eq(Roles.ROLE_USER.getName()))).willReturn(new Role());
        given(userMapper.mapToEntity(any(UserDTO.class))).willReturn(user);
        given(hashingService.hashPassword(any(String.class))).willReturn(new byte[32]);
        given(userRepositorySecurity.save(any(User.class))).willReturn(user);
        given(jwtProviderImpl.generateAccessToken(user)).willReturn("accessToken");
        given(jwtProviderImpl.generateRefreshToken(user)).willReturn("refreshToken");

        // When
        JwtResponse response = authService.registration(userDTO);

        // Then
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepositorySecurity, times(1)).findUserByLogin(userDTO.getLogin());
        verify(roleRepositorySecurity, times(1)).findByName(eq(Roles.ROLE_USER.getName()));
        verify(userMapper, times(1)).mapToEntity(any(UserDTO.class));
        verify(hashingService, times(1)).hashPassword(any(String.class));
        verify(userRepositorySecurity, times(1)).save(any(User.class));
        verify(jwtProviderImpl, times(1)).generateAccessToken(any(User.class));
        verify(jwtProviderImpl, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    void login_ExistingUser_ReturnsJwtResponse() {
        // Given
        JwtRequest authRequest = new JwtRequest();
        authRequest.setLogin("testUser");
        authRequest.setPassword("password");
        User user = new User();
        user.setLogin("testUser");
        user.setPassword(HexUtils.toHexString("password".getBytes())); // Assuming password is a hex-encoded string
        given(userService.getByLogin(authRequest.getLogin())).willReturn(Optional.of(user));
        given(hashingService.hashPassword(authRequest.getPassword())).willReturn(HexUtils.fromHexString(user.getPassword())); // Return password as bytes
        given(jwtProviderImpl.generateAccessToken(any(User.class))).willReturn("accessToken");
        given(jwtProviderImpl.generateRefreshToken(any(User.class))).willReturn("refreshToken");

        // When
        JwtResponse response = authService.login(authRequest);

        // Then
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userService, times(1)).getByLogin(authRequest.getLogin());
        verify(hashingService, times(1)).hashPassword(authRequest.getPassword());
        verify(jwtProviderImpl, times(1)).generateAccessToken(any(User.class));
        verify(jwtProviderImpl, times(1)).generateRefreshToken(any(User.class));
    }

    @Test
    void login_NonExistingUser_ReturnsNullJwtResponse() {
        // Given
        JwtRequest authRequest = new JwtRequest();
        authRequest.setLogin("testUser");
        authRequest.setPassword("password");
        given(userService.getByLogin(authRequest.getLogin())).willReturn(Optional.empty());

        // When
        JwtResponse response = authService.login(authRequest);

        // Then
        assertNull(response.getAccessToken());
        assertNull(response.getRefreshToken());
        verify(userService, times(1)).getByLogin(authRequest.getLogin());
    }

    @Test
    void getAccessToken_ValidRefreshToken_ReturnsJwtResponseWithAccessToken() {
        // Given
        String refreshToken = "validRefreshToken";
        Claims claims = createMockClaims("testUser");
        given(jwtProviderImpl.validateRefreshToken(refreshToken)).willReturn(true);
        given(jwtProviderImpl.getRefreshClaims(refreshToken)).willReturn(claims);
        given(refreshStorage.get("testUser")).willReturn(refreshToken);
        User user = new User();
        user.setLogin("testUser");
        given(userService.getByLogin("testUser")).willReturn(Optional.of(user));
        given(jwtProviderImpl.generateAccessToken(user)).willReturn("newAccessToken");

        // When
        JwtResponse response = authService.getAccessToken(refreshToken);

        // Then
        assertNull(response.getRefreshToken());
        verify(jwtProviderImpl, times(1)).validateRefreshToken(refreshToken);
        verify(jwtProviderImpl, times(1)).getRefreshClaims(refreshToken);
    }

    @Test
    void refresh_InvalidRefreshToken_ThrowsJwtException() {
        // Given
        String invalidRefreshToken = "invalidRefreshToken";
        given(jwtProviderImpl.validateRefreshToken(invalidRefreshToken)).willReturn(false);

        // When / Then
        assertThrows(JwtException.class, () -> authService.refresh(invalidRefreshToken));
        verify(jwtProviderImpl, times(1)).validateRefreshToken(invalidRefreshToken);
    }

    private Claims createMockClaims(String subject) {
        Claims claims = Mockito.mock(Claims.class);
        given(claims.getSubject()).willReturn(subject);
        return claims;
    }
}