package itmo.org.learningenglishmanagmentsystem.security.service.impl;

import io.jsonwebtoken.Claims;
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
import itmo.org.learningenglishmanagmentsystem.security.service.AuthService;
import itmo.org.learningenglishmanagmentsystem.security.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepositorySecurity userRepositorySecurity;
    private final Map<String, String> refreshStorage = new HashMap<>();
    private final JwtProviderImpl jwtProviderImpl;
    private final UserMapper userMapper;
    private final HashingServiceImpl hashingService;
    private final RoleRepositorySecurity roleRepositorySecurity;

    @Override
    public JwtResponse registration(UserDTO userDTO) {
        Optional<User> existingUser = userRepositorySecurity.findUserByLogin(userDTO.getLogin());
        if (existingUser.isPresent()) {
            // пользователь с таким логином уже есть в системе
            return new JwtResponse(null, null);
        }

        User user = userMapper.mapToEntity(userDTO);
        user.setRoles(new HashSet<>(Collections.singletonList(roleRepositorySecurity.findByName(Roles.ROLE_USER.getName()))));
        user.setPassword(HexUtils.toHexString(hashingService.hashPassword(user.getPassword())));
        User save = userRepositorySecurity.save(user);
        final String accessToken = jwtProviderImpl.generateAccessToken(save);
        final String refreshToken = jwtProviderImpl.generateRefreshToken(save);
        refreshStorage.put(save.getLogin(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        Optional<User> optionalUser = userService.getByLogin(authRequest.getLogin());
        if (optionalUser.isEmpty()) {
            return new JwtResponse(null, null);
        }

        User user = optionalUser.get();
        byte[] authPassword = hashingService.hashPassword(authRequest.getPassword());
        byte[] actualPassword = HexUtils.fromHexString(user.getPassword());
        if (!Arrays.equals(actualPassword, authPassword)) {
            return new JwtResponse(null, null);
        }

        final String accessToken = jwtProviderImpl.generateAccessToken(user);
        final String refreshToken = jwtProviderImpl.generateRefreshToken(user);
        refreshStorage.put(user.getLogin(), refreshToken);
        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProviderImpl.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProviderImpl.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Optional<User> user = userService.getByLogin(login);
                if (user.isEmpty()) {
                    return new JwtResponse(null, null);
                }

                final String accessToken = jwtProviderImpl.generateAccessToken(user.get());
                return new JwtResponse(accessToken, null);
            }
        }
        return new JwtResponse(null, null);
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProviderImpl.validateRefreshToken(refreshToken)) {
            final Claims claims = jwtProviderImpl.getRefreshClaims(refreshToken);
            final String login = claims.getSubject();
            final String saveRefreshToken = refreshStorage.get(login);
            if (saveRefreshToken != null && saveRefreshToken.equals(refreshToken)) {
                final Optional<User> optionalUser = userService.getByLogin(login);
                if (optionalUser.isEmpty()) {
                    return new JwtResponse(null, null);
                }

                final User user = optionalUser.get();
                final String accessToken = jwtProviderImpl.generateAccessToken(user);
                final String newRefreshToken = jwtProviderImpl.generateRefreshToken(user);
                refreshStorage.put(user.getLogin(), newRefreshToken);
                return new JwtResponse(accessToken, newRefreshToken);
            }
        }
        throw new JwtException("Невалидный JWT токен");
    }
}