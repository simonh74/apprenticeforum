package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    public int role_id;

    @Getter
    @Setter
    @Column(name = "description")
    public String description;

    @Getter
    @Setter
    @ManyToMany(mappedBy = "userroles", fetch = FetchType.EAGER)
    public Set<User> roleofusers;

    public Role() {
        super();
    }

    public Role(int role_id, String description) {
        this.role_id = role_id;
        this.description = description;
    }
}
