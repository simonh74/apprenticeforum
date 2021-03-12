package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.Set;

@Entity
@Table(name = "post")
//Vererbung "simulieren"
@Inheritance(
        strategy = InheritanceType.JOINED
)
public class Post
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    public int post_id;

    @Getter
    @Setter
    @Column(name = "date_posted")
    private Date date_posted;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "downvoted_posts", fetch = FetchType.EAGER)
    public Set<User> downvoted_from_users;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "upvoted_posts", fetch = FetchType.EAGER)
    public Set<User> upvoted_from_users;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User posted_from_user;

    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discussion_id", nullable = false)
    public Discussion posted_in_discussion;

    public Post() {
        super();
    }

    public Post(int post_id, Date date_posted) {
        this.post_id = post_id;
        this.date_posted = date_posted;
    }
}
