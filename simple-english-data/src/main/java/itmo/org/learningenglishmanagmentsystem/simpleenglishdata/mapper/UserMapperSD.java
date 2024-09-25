package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper;

import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapperSD {
    public User mapToEntity(UserDto userDto) {
        User user = new User();
        user.setLogin(userDto.getLogin());
        user.setPassword(userDto.getPassword());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}
