package itmo.org.learningenglishmanagmentsystem.security.service.impl;

import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.security.repository.UserRepositorySecurity;
import itmo.org.learningenglishmanagmentsystem.security.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepositorySecurity userRepositorySecurity;
    @Override
    public Optional<User> getByLogin(@NonNull String login) {
        return userRepositorySecurity.findUserByLogin(login);
    }
}
