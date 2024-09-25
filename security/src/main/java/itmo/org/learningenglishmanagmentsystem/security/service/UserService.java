package itmo.org.learningenglishmanagmentsystem.security.service;

import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import lombok.NonNull;

import java.util.Optional;

public interface UserService {
    Optional<User> getByLogin(@NonNull String login);
}
