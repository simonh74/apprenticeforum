package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.*;
import ch.cognizant.sh.apprenticeforum.service.DiscussionService;
import ch.cognizant.sh.apprenticeforum.service.PostService;
import ch.cognizant.sh.apprenticeforum.service.QuestionService;
import ch.cognizant.sh.apprenticeforum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class ForumController
{
    @Autowired
    public QuestionService questionService;

    @Autowired
    public UserService userService;

    @Autowired
    public DiscussionService discussionService;

    @Autowired
    public PostService postService;


    @GetMapping("/forum")
    public String showForumPage(Model model) {
        Collection<Post> latest_questions = getLatestQuestions(10);

        List<List<Post>> two_dim_q_and_a = getTwoListsOfAnsweredAndMyQuestions(10, 10);
        List<Post> my_posted_questions = two_dim_q_and_a.get(0);
        List<Post> discussions_with_my_answer = two_dim_q_and_a.get(1);

        //make those lists available/accessible in the html
        model.addAttribute("listOfLatestDiscussions", latest_questions);
        model.addAttribute("listOfMyPostedQuestions", my_posted_questions);
        model.addAttribute("listOfDiscussionsWithMyAnswer", discussions_with_my_answer);

        return "forum";
    }

    @GetMapping("/ask-question")
    public String showAskNewQuestionPage(Model model) {
        model.addAttribute("question", new Question());
        return "ask-question";
    }

    @PostMapping("/ask-question")
    public String postNewDiscussion(@Valid @ModelAttribute Question question, BindingResult result, Model model)
    {
        //check if the form as any validation errors
        if(result.hasErrors()) {
            return "ask-question";
        } else {
            //first we create the new discussion entity
            Discussion empty_new_discussion = new Discussion(false);

            //extract the currently logged in user
            User loggedin_user = getCurrentlyLoggedInUser();

            //add members to the discussion (one to many)
            //I'm creating a new arraylist here, because discussion doesn't in the database yet
            Collection<User> discussion_members = new ArrayList<>();
            discussion_members.add(loggedin_user);
            empty_new_discussion.setDiscussionmembers(discussion_members);

            //add the discussion with the list of members to the database
            discussionService.add(empty_new_discussion);

            //set attributes for the question
            //get current date and time
            Date date_of_now = new Date(System.currentTimeMillis());
            question.setDate_posted(date_of_now);
            question.setPosted_from_user(loggedin_user);
            question.setPosted_in_discussion(empty_new_discussion);

            //add the discussion to the member (one to many)
            loggedin_user.getMemberofdiscussion().add(empty_new_discussion);

            //add question to the database incl. foreign keys for user and discussion
            questionService.add(question);

            return "forum";
        }
    }

    @GetMapping("/view-question")
    public String showDiscussionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //get question by the id passed in the URL
        Question question_to_show = questionService.getById(id);

        //get the discussion from the question
        Discussion discussion_to_show = discussionService.getById(question_to_show.getPosted_in_discussion().getDiscussion_id());

        //prepare a new empty Answer object for the form below the answers
        Answer answer = new Answer();
        answer.setPosted_in_discussion(discussion_to_show);

        //gather together the list of answers of the discussion
        //Here we are looping through every post of the discussion and exclude the post of type: question
        List<Post> listOfAnswers = discussion_to_show.getDiscussionListOfPosts();
        for(Post postitr : listOfAnswers) {
            //check if post is a question
            if(postitr instanceof Question) {
                //if true: add
                model.addAttribute("questionOfDiscussion", postitr);
                listOfAnswers.remove(postitr);
                break;
            }
        }

        //pass list of answers and a new possible new answer to the html
        model.addAttribute("answer", answer);
        model.addAttribute("listOfAnswers", listOfAnswers);

        return "view-discussion";
    }

    private User getCurrentlyLoggedInUser() {
        //get current logged in user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currently_logged_in_user;

        if(principal instanceof UserDetails) {
            currently_logged_in_user = userService.getByEmail(((UserDetails) principal).getUsername());
        } else {
            currently_logged_in_user = userService.getByEmail(principal.toString());
        }
        return currently_logged_in_user;
    }

    private Collection<Post> getLatestQuestions(int amount) {
        //gather the latest 10 posts in one list
        Collection<Post> latest_posts = new ArrayList<>();

        int counter = 0;
        for(Post postir : postService.getAll())
            if(counter <= amount) {
                latest_posts.add(postir);
                counter++;
            } else {
                //stop and step out of the for loop
                break;
            }
        return latest_posts;
    }

    private List<List<Post>> getTwoListsOfAnsweredAndMyQuestions(int amount1, int amount2) {
        //get the logged in user
        User loggin_user = getCurrentlyLoggedInUser();

        //prepare two lists
        // 1. all discussion with my posted question
        // 2. all discussion with my answer in it
        List<Post> my_posted_questions = new ArrayList<>();
        List<Post> discussions_with_my_answer = new ArrayList<>();

        int counter1 = 0;
        int counter2 = 0;
        //loop through all posts until the amount number is reached
        for(Post postitr : postService.getAll()) {
            //check if the post author is equal to the currently logged in user
            if(postitr.getPosted_from_user() == loggin_user) {
                //check if the post is of the type Question
                if(postitr instanceof Question) {
                    if(counter1 < amount1) {
                        //if true: add to list of my posted questions (list. 1)
                        my_posted_questions.add(postitr);
                        counter1++;
                    }
                }
                else {
                    if(counter2 < amount2) {
                        //if false: the post is an answer -> get the question of the discussion and add it to the list nr. 2
                        Discussion mydisc = postitr.getPosted_in_discussion();
                        discussions_with_my_answer.add(mydisc.getDiscussionListOfPosts().get(0));
                        counter2++;
                    }
                }
            }
        }

        //create new two dimensional arraylist
        List<List<Post>> two_dim_q_and_a = new ArrayList<>();

        //add our two lists into one 2D array, because we can't return 2 values.
        two_dim_q_and_a.add(my_posted_questions);
        two_dim_q_and_a.add(discussions_with_my_answer);

        return two_dim_q_and_a;
    }
}
