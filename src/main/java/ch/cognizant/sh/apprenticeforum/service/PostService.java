package ch.cognizant.sh.apprenticeforum.service;

import ch.cognizant.sh.apprenticeforum.model.Post;
import ch.cognizant.sh.apprenticeforum.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService
{
    @Autowired
    private PostRepository repository;

    public Iterable<Post> getAll() {
        return repository.findAll();
    }

    public Post getById(Integer id) {
        Iterable<Post> postitr = repository.findAll();

        for(Post post : postitr) {
            if (post.getPost_id() == id) {
                return post;
            }
        }
        //id does not exist
        return null;
    }

    public void add(Post post) {
        repository.save(post);
    }

    public void update(Long id, Post post) {
        //save works also for update
        repository.save(post);
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }
}
