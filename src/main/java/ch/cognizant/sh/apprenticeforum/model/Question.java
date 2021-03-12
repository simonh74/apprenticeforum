package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "question")
public class Question extends Post
{
    //The id of the question is the post_id

    /*@Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    public int question_id;*/

    @Getter
    @Setter
    @Column(name = "title")
    public String title;

    @Getter
    @Setter
    @Column(name = "content")
    public String content;

    public Question() {
        super();
    }

    public Question(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
