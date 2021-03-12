package ch.cognizant.sh.apprenticeforum.repository;

import ch.cognizant.sh.apprenticeforum.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer>
{

}
