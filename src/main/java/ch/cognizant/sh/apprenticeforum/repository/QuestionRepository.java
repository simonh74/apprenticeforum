package ch.cognizant.sh.apprenticeforum.repository;

import ch.cognizant.sh.apprenticeforum.model.Question;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, Integer> {

}
