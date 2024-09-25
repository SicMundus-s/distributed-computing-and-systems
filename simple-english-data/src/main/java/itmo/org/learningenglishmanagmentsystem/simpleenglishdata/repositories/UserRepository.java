package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories;


import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByLogin(String login);
    User findByLogin(String username);
    void deleteByLogin(String login);


}
