package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
public class Answer extends Post
{
    //The id of the question is the post_id

    /*@Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    public int answer_id;*/

    @Getter
    @Setter
    @Column(name = "content", columnDefinition = "VARCHAR(10000)")
    public String content;

    public Answer() {
        super();
    }

    public Answer(String content) {
        this.content = content;
    }
}
