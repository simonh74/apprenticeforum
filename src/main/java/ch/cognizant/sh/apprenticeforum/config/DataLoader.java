package ch.cognizant.sh.apprenticeforum.config;

import ch.cognizant.sh.apprenticeforum.model.Role;
import ch.cognizant.sh.apprenticeforum.repository.RoleRepository;
import ch.cognizant.sh.apprenticeforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class DataLoader implements ApplicationRunner {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void run(ApplicationArguments args) {
        ArrayList<Role> listOfRoles = new ArrayList<Role>();
        listOfRoles.add(new Role(1, "ROLE_MEMBER"));
        listOfRoles.add(new Role(2, "ROLE_ADMIN"));
        listOfRoles.add(new Role(3, "ROLE_SUPERVISOR"));

        roleRepository.saveAll(listOfRoles);

    }
}
