package ch.cognizant.sh.apprenticeforum.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
    @Size(min=10, max=255, message = "Length of content must be between 10 and a 255 characters.")
    @Column(name = "content")
    public String content;

    public Answer() {
        super();
    }

    public Answer(String content) {
        this.content = content;
    }
}
