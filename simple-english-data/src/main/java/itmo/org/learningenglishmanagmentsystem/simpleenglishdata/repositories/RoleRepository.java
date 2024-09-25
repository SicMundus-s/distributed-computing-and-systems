package itmo.org.learningenglishmanagmentsystem.simpleenglishdata.repositories;


import itmo.org.learningenglishmanagmentsystem.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);
}
