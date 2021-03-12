package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.Discussion;
import ch.cognizant.sh.apprenticeforum.repository.DiscussionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscussionService
{
    @Autowired
    private DiscussionRepository repository;

    public Iterable<Discussion> getAll() {
        return repository.findAll();
    }

    public Discussion getById(Integer id) {
        Iterable<Discussion> discussionitr = repository.findAll();

        for(Discussion discussion : discussionitr) {
            if(discussion.getDiscussion_id() == id) {
                return discussion;
            }
        }
        //id does not exist
        return null;
    }

    public void add(Discussion discussion) {
        repository.save(discussion);
    }

    public void update(Long id, Discussion discussion) {
        //save works also for update
        repository.save(discussion);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
