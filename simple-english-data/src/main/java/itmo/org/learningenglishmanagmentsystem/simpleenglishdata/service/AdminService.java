package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;


import itmo.org.learningenglishmanagmentsystem.core.entity.Role;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.RoleRepository;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Transactional
    public void deleteUser(String login) {
        userRepository.deleteByLogin(login);
    }

    public void setAdmin(User user) {
        Role userRole = roleRepository.findByName("ROLE_ADMIN");
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        userRepository.save(user);
    }
}
