package itmo.org.learningenglishmanagmentsystem.security.securitys;


import itmo.org.learningenglishmanagmentsystem.core.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public class SecurityRole implements GrantedAuthority {

    private final Role role;

    @Override
    public String getAuthority() {
        return role.getName();
    }
}
