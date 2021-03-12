package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.Answer;
import ch.cognizant.sh.apprenticeforum.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerService
{
    @Autowired
    private AnswerRepository repository;

    public Iterable<Answer> getAll() {
        return repository.findAll();
    }

    public Answer getById(Integer id) {
        Iterable<Answer> answeritr = repository.findAll();

        for(Answer answer : answeritr) {
            if (answer.getPost_id() == id) {
                return answer;
            }
        }
        //id does not exist
        return null;
    }

    public void add(Answer answer) {
        repository.save(answer);
    }

    public void update(Long id, Answer answer) {
        //"save" works also for update
        repository.save(answer);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
