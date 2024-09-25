package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;

import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.mapper.UserMapperSD;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperSD userMapperSD;

    @InjectMocks
    private UserService userService;


    @Test
    public void testFindByLogin() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testUser");

        BDDMockito.given(userRepository.findByLogin("testUser")).willReturn(testUser);

        User result = userService.findByLogin("testUser");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLogin()).isEqualTo("testUser");
    }

    @Test
    public void testFindById() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testUser");

        BDDMockito.given(userRepository.findById(1L)).willReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getLogin()).isEqualTo("testUser");
    }

    @Test
    public void testOptionalFindByLogin() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setLogin("testUser");

        BDDMockito.given(userRepository.findUserByLogin("testUser")).willReturn(Optional.of(testUser));

        Optional<User> result = userService.OptionalFindByLogin("testUser");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getLogin()).isEqualTo("testUser");
    }

    @Test
    public void testGetLogin() {
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        BDDMockito.given(authentication.getPrincipal()).willReturn(userDetails);
        BDDMockito.given(userDetails.getUsername()).willReturn("testUser");

        String login = userService.getLogin(authentication);

        assertThat(login).isEqualTo("testUser");
    }
}
