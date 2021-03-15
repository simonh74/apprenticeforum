package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "discussion")
public class Discussion
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discussion_id")
    public int discussion_id;

    @Getter
    @Setter
    @Column(name = "resolved")
    public boolean resolved;

    //Bidirectional @OneToMany with @JoinColumn
    @Getter
    @Setter
    @OneToMany(mappedBy = "posted_in_discussion", cascade = { CascadeType.MERGE, CascadeType.REMOVE,
            CascadeType.REFRESH, CascadeType.DETACH }, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Post> discussionListOfPosts = new ArrayList<>();


    @Getter
    @Setter
    @JoinTable(name = "DISCUSION_USER", joinColumns = {
            @JoinColumn(name = "discussion_id", referencedColumnName = "discussion_id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)})
    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.FALSE)
    private Collection<User> discussionmembers;

    public Discussion() {
        super();
    }

    public Discussion(boolean resolved) {
        this.resolved = resolved;
    }
}
