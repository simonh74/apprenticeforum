package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.Role;
import ch.cognizant.sh.apprenticeforum.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService
{
    @Autowired
    RoleRepository repository;

    public Iterable<Role> getAll() {
        return repository.findAll();
    }

    public Role getById(Integer id) {
        Iterable<Role> roleitr = repository.findAll();

        for (Role role : roleitr) {
            if (role.getRole_id() == id) {
                return role;
            }
        }
        //id does not exist
        return null;
    }

    public void add(Role role) {
        repository.save(role);
    }

    public void update(int id, Role role) {
        //save works also for update
        repository.save(role);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
