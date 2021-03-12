package ch.cognizant.sh.apprenticeforum.repository;

import ch.cognizant.sh.apprenticeforum.model.Post;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<Post, Integer>
{

}
