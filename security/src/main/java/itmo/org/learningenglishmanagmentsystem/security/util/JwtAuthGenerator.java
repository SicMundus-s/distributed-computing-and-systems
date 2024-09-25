package itmo.org.learningenglishmanagmentsystem.security.util;

import io.jsonwebtoken.Claims;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.security.entity.jwt.JwtAuth;
import itmo.org.learningenglishmanagmentsystem.security.repository.UserRepositorySecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;


@RequiredArgsConstructor
@Component
public class JwtAuthGenerator {

    private final UserRepositorySecurity userRepositorySecurity;
    public JwtAuth generate(Claims claims) {
        Optional<User> userByLogin = userRepositorySecurity.findUserByLogin(claims.getSubject());
        final JwtAuth jwtInfoToken = new JwtAuth(userByLogin.get());
        jwtInfoToken.setFirstName(claims.get("firstName", String.class));
        jwtInfoToken.setUsername(claims.getSubject());
        return jwtInfoToken;
    }
}