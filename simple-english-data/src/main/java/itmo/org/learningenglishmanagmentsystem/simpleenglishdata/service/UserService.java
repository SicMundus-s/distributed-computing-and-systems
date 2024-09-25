package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;


import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.security.securitys.HashingServiceImpl;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.UserDto;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.UserMapperSD;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapperSD userMapperSD;
    private final HashingServiceImpl hashingService;

    public User findByLogin(String username) {
        return userRepository.findByLogin(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> OptionalFindByLogin(String username) {
        return userRepository.findUserByLogin(username);
    }

    public Long update(UserDto userDto, Long id) {
        User user = userMapperSD.mapToEntity(userDto);
        user.setId(id);
        user.setPassword(HexUtils.toHexString(hashingService.hashPassword(user.getPassword())));
        return userRepository.save(user).getId();
    }

    public String getLogin(Authentication authentication) {
        String login;
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            login = ((UserDetails) principal).getUsername();
        } else {
            login = principal.toString();
        }
        return login;
    }

}
