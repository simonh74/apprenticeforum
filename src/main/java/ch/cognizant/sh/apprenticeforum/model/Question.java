package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
    @Size(min=10, max=100, message = "Length of the title must be between 10 and a 100 characters.")
    @Column(name = "title")
    public String title;

    @Getter
    @Setter
    @Size(min=10, max=255, message = "Length of content must be between 10 and a 255 characters.")
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
