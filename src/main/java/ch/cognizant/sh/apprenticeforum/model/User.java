package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    public int user_id;

    @Getter
    @Setter
    @Column(name = "firstname")
    public String firstname;

    @Getter
    @Setter
    @Column(name = "lastname")
    public String lastname;

    @Getter
    @Setter
    @Column(name = "email")
    public String email;

    @Getter
    @Setter
    @Column(name = "cognizantid")
    public int congizantid;

    @Getter
    @Setter
    @Column(name = "password")
    public String password;

    @Getter
    @Setter
    @Column(name = "verified")
    public boolean verified;

    @Getter
    @Setter
    @Column(name = "verification_code")
    public int verification_code;

    @Getter
    @Setter
    @JoinTable(name = "USER_ROLES", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)}, inverseJoinColumns = {
            @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false)})
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    public Set<Role> userroles;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "discussionmembers")
    private Collection<Discussion> memberofdiscussion;

    //Bidirectional @oneToMany with @JoinColumn
    @Getter
    @Setter
    @OneToMany(mappedBy = "posted_from_user", cascade = { CascadeType.MERGE, CascadeType.REMOVE,
    CascadeType.REFRESH, CascadeType.DETACH}, orphanRemoval = true, fetch = FetchType.EAGER)
    public List<Post> userListOfPosts = new ArrayList<>();

    public User() {
        super();
    }

    public User(int user_id, String firstname, String lastname, String email, int congizantid, String password, boolean verified, int verification_code) {
        this.user_id = user_id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.congizantid = congizantid;
        this.password = password;
        this.verified = verified;
        this.verification_code = verification_code;
    }
}
