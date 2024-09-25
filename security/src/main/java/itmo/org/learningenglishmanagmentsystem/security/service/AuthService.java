package itmo.org.learningenglishmanagmentsystem.security.service;


import itmo.org.learningenglishmanagmentsystem.security.dto.UserDTO;
import itmo.org.learningenglishmanagmentsystem.security.entity.jwt.JwtRequest;
import itmo.org.learningenglishmanagmentsystem.security.entity.jwt.JwtResponse;

public interface AuthService {

    JwtResponse registration(UserDTO userDTO);
    JwtResponse login(JwtRequest authRequest);
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refresh(String refreshToken);
}
