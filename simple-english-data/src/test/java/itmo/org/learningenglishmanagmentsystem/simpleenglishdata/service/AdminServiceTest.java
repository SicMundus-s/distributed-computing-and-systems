package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.service;

import itmo.org.learningenglishmanagmentsystem.core.entity.Role;
import itmo.org.learningenglishmanagmentsystem.core.entity.User;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.RoleRepository;
import itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminService adminService;

    @Test
    public void whenDeleteUser_thenReturnSuccess() {
        // Given
        String login = "testUser";

        // When
        doNothing().when(userRepository).deleteByLogin(login);
        adminService.deleteUser(login);

        // Then
        verify(userRepository, times(1)).deleteByLogin(login);
    }

    @Test
    public void whenSetAdmin_thenReturnUserWithAdminRole() {
        // Given
        User testUser = new User();
        Role adminRole = new Role();

        given(roleRepository.findByName("ROLE_ADMIN")).willReturn(adminRole);
        given(userRepository.save(testUser)).willReturn(testUser);

        // When
        adminService.setAdmin(testUser);

        // Then
        assertThat(testUser.getRoles()).contains(adminRole);
        verify(roleRepository, times(1)).findByName("ROLE_ADMIN");
        verify(userRepository, times(1)).save(testUser);
    }
}