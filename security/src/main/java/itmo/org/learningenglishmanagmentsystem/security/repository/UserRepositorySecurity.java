package itmo.org.learningenglishmanagmentsystem.security.repository;

import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositorySecurity extends JpaRepository<User, Long> {

    Optional<User> findUserByLogin(String login);
}