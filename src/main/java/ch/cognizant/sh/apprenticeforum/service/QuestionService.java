package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.Question;
import ch.cognizant.sh.apprenticeforum.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService
{
    @Autowired
    private QuestionRepository repository;

    public Iterable<Question> getAll() {
        return repository.findAll();
    }

    public Question getById(Integer id) {
        Iterable<Question> questionitr = repository.findAll();

        for(Question question : questionitr) {
            if (question.getPost_id() == id) {
                return question;
            }
        }
        //id does not exist
        return null;
    }

    public void add(Question question) {
        repository.save(question);
    }

    public void update(Long id, Question question) {
        //save works also for update
        repository.save(question);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
