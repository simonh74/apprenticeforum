package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.User;
import ch.cognizant.sh.apprenticeforum.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    UserRepository repository;

    public Iterable<User> getAll(Integer id) {
        return repository.findAll();
    }

    public User getById(Integer id) {
        Iterable<User> useritr = repository.findAll();

        for(User user : useritr) {
            if (user.getUser_id() == id) {
                return user;
            }
        }
        //id does not exist
        return null;
    }

    public void add(User user) {
        repository.save(user);
    }

    public void update(int id, User user) {
        //save works also for update
        repository.save(user);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    public User getByEmail(String email) {
        Iterable<User> useritr = repository.findAll();

        for (User user : useritr) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        //nothing found
        return null;
    }
}
