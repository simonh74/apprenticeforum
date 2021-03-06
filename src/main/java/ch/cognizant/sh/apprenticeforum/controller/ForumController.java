package ch.cognizant.sh.apprenticeforum.controller;

import ch.cognizant.sh.apprenticeforum.model.*;
import ch.cognizant.sh.apprenticeforum.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
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
    public AnswerService answerService;

    @Autowired
    public UserService userService;

    @Autowired
    public DiscussionService discussionService;

    @Autowired
    public PostService postService;

    @Autowired
    public EmailSenderService emailSenderService;


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

        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());
        return "forum";
    }

    @GetMapping("/ask-question")
    public String showAskNewQuestionPage(Model model) {
        model.addAttribute("question", new Question());

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "ask-question";
    }

    @PostMapping("/ask-question")
    public String postNewDiscussion(@Valid @ModelAttribute Question question, BindingResult result, Model model)
    {
        //check if the form as any validation errors
        if(result.hasErrors()) {
            //for showing the name in the navbar only
            model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

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

            //send email to everyone, that a new question has been posted
            try {
                emailSenderService.sendNewQuestionNotificationToEveryone(loggedin_user, question.getTitle());
            } catch (MailException e) {
                e.printStackTrace();
            }

            return "redirect:/forum";
        }
    }

    @PostMapping("/answer-question")
    public String processAnswerQuestion(@Valid @ModelAttribute Answer answer, BindingResult result, Model model) {
        //here we are creating a answer and attach it to the list of posts of the discussion
        //check if the form as any validation errors
        if(result.hasErrors()) {
            //for showing the name in the navbar only
            model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

            return "view-discussion";
        }
        else {
            //get discussion from the answer
            Discussion discussionWithTheAnswer = answer.getPosted_in_discussion();

            //add answer to to list of posts in the discussion
            discussionWithTheAnswer.getDiscussionListOfPosts().add(answer);

            //get Question of the discussion
            Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithTheAnswer);

            //add today's date to the answer
            Date date_of_now = new Date(System.currentTimeMillis());
            answer.setDate_posted(date_of_now);

            //save answer to the database
            answerService.add(answer);

            return "redirect:/view-discussion?id=" + questionOfDiscussion.getPost_id();
        }
    }

    @GetMapping("/view-discussion")
    public String showDiscussionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //get question by the id passed in the URL
        Question question_to_show = questionService.getById(id);

        //get the discussion from the question
        Discussion discussion_to_show = discussionService.getById(question_to_show.getPosted_in_discussion().getDiscussion_id());

        //prepare a new empty Answer object for the form below the answers
        Answer answer = new Answer();
        answer.setPosted_in_discussion(discussion_to_show);
        answer.setPosted_from_user(getCurrentlyLoggedInUser());

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

        //add logged in user to the model -> to show edit and delete button if the logged in user equals the author of the post
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "view-discussion";
    }

    @GetMapping("/edit-question")
    public String showEditQuestionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //get the question object by the id passed in the URL
        Question questionOfDiscussion = questionService.getById(id);

        model.addAttribute("question", questionOfDiscussion);

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "edit-question";
    }

    @PostMapping("/edit-question")
    public String processEditQuestion(@Valid @ModelAttribute Question question, BindingResult result, Model model) {
        //check if the form as any validation errors
        if(result.hasErrors()) {
            //for showing the name in the navbar only
            model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

            return "edit-question";
        } else {
            //update the question in the database
            questionService.update((long) question.getPost_id(), question);

            //return back to the view-discussion page -> therefore we need the id of the question of the discussion
            return "redirect:/view-discussion?id=" + question.getPost_id();
        }
    }

    @GetMapping("/edit-answer")
    public String showEditAnswerPage(@RequestParam(name="id", required = true) int id, Model model) {
        //get the answer object by the id passed in the URL
        Answer answerOfDiscussion = answerService.getById(id);

        //get the question of the discussion
        Discussion discussionWithTheAnswer = answerOfDiscussion.getPosted_in_discussion();
        Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithTheAnswer);

        model.addAttribute("answer", answerOfDiscussion);
        model.addAttribute("questionOfDiscussion", questionOfDiscussion);

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "edit-answer";
    }

    @PostMapping("/edit-answer")
    public String processEditAnswer(@Valid @ModelAttribute Answer answer, BindingResult result, Model model) {
        //check if the form as any validation errors
        if(result.hasErrors()) {
            //for showing the name in the navbar only
            model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

            return "edit-answer";
        } else {
            //update the answer in the database
            answerService.update((long) answer.getPost_id(), answer);

            //return back to the view-discussion page -> therefore we need the id of the question of the discussion
            Discussion discussionWithTheAnswer = answer.getPosted_in_discussion();
            Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithTheAnswer);

            return "redirect:/view-discussion?id=" + questionOfDiscussion.getPost_id();
        }
    }

    @GetMapping("/delete-question")
    public String deleteQuestionAndDiscussion(@RequestParam(name="id", required = true) int id, Model model) {
        //get the question by id
        Question questionOfDiscussion = questionService.getById(id);

        //get discussion from the question
        Discussion discussionWithTheQuestion = questionOfDiscussion.getPosted_in_discussion();

        //for cleaning up foreign keys, we need to remove upvote/downvote lists of all posts in the discussion
        for(Post postitr : discussionWithTheQuestion.getDiscussionListOfPosts()) {
            //remove downvote connections from the post's and from the user's perspective
            postitr.getDownvoted_from_users().clear();
            postitr.getPosted_from_user().getDownvoted_posts().clear();

            //remove upvote connections from the post's and from the user's perspective
            postitr.getUpvoted_from_users().clear();
            postitr.getPosted_from_user().getUpvoted_posts().clear();

            postService.update((long) postitr.post_id, postitr);
        }

        //for cleaning up foreign keys, we need to remove the question from the discussion
        discussionWithTheQuestion.getDiscussionListOfPosts().remove(questionOfDiscussion);


        //delete question from the database
        questionService.deleteById(id);

        //delete discussion from database, because there is no discussion without an answer
        discussionService.deleteById(discussionWithTheQuestion.getDiscussion_id());

        //return back to forum, because the discussion doesn't exist anymore
        return "redirect:/forum";
    }

    @GetMapping("/delete-answer")
    public String deleteAnswer(@RequestParam(name="id", required = true) int id, Model model) {
        //get the Answer object by the id
        Answer answerOfDiscussion = answerService.getById(id);

        //get Discussion from the answer
        Discussion discussionWithTheAnswer = answerOfDiscussion.getPosted_in_discussion();

        //to clean up the foreign keys, we first need to delete the answer (post) from the discussion
        discussionWithTheAnswer.getDiscussionListOfPosts().remove(answerOfDiscussion);

        //now we can delete the answer from the database
        answerService.deleteById(id);

        //return back to the view-discussion page -> therefore we need the id of the question of the discussion
        Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithTheAnswer);

        return "redirect:/view-discussion?id=" + questionOfDiscussion.getPost_id();
    }

    @GetMapping("/search-question")
    public String showSearchQuestionPage(Model model) {
        //get all question in the database
        Collection<Post> allPostsFromDb = (Collection<Post>) postService.getAll();
        Collection<Question> allQuestionsFromDb = new ArrayList<>();
        for(Post postitr : allPostsFromDb) {
            if(postitr instanceof Question) {
                allQuestionsFromDb.add((Question) postitr);
            }
        }
        model.addAttribute("questionlist", allQuestionsFromDb);

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "search-question";
    }

    @GetMapping("/view-discussion/upvote-post")
    public String upvotePostFromViewDiscussionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        upvotePost(id);

        //get the question id from the upvoted post
        Discussion discussionWithPost = postService.getById(id).getPosted_in_discussion();
        Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithPost);

        return "redirect:/view-discussion?id=" + questionOfDiscussion.getPost_id();
    }

    @GetMapping("/view-discussion/downvote-post")
    public String downvotePostFromViewDiscussionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        downvotePost(id);

        //get the question id from the upvoted post
        Discussion discussionWithPost = postService.getById(id).getPosted_in_discussion();
        Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithPost);

        return "redirect:/view-discussion?id=" + questionOfDiscussion.getPost_id();
    }

    @GetMapping("/forum/upvote-post")
    public String upvotePostFromForum(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        upvotePost(id);

        return "redirect:/forum";
    }

    @GetMapping("/forum/downvote-post")
    public String downvotePostFromForumPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        downvotePost(id);

        return "redirect:/forum";
    }

    @GetMapping("/search-question/upvote-post")
    public String upvotePostFromSearchQuestionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        upvotePost(id);

        return "redirect:/search-question";
    }

    @GetMapping("/search-question/downvote-post")
    public String downvotePostFromSearchQuestionPage(@RequestParam(name="id", required = true) int id, Model model) {
        //downvote post
        downvotePost(id);

        return "redirect:/search-question";
    }

    @GetMapping("/search-my-questions/upvote-post")
    public String upvotePostFromSearchMyQuestionsPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        upvotePost(id);

        return "redirect:/view-all-my-questions";
    }

    @GetMapping("/search-my-questions/downvote-post")
    public String downvotePostFromSearchMyQuestionsPage(@RequestParam(name="id", required = true) int id, Model model) {
        //downvote post
        downvotePost(id);

        return "redirect:/view-all-my-questions";
    }

    @GetMapping("/search-my-answers/upvote-post")
    public String upvotePostFromSearchMyAnswersPage(@RequestParam(name="id", required = true) int id, Model model) {
        //upvote post
        upvotePost(id);

        return "redirect:/view-all-my-answers";
    }

    @GetMapping("/search-my-answers/downvote-post")
    public String downvotePostFromSearchMyAnswersPage(@RequestParam(name="id", required = true) int id, Model model) {
        //downvote post
        downvotePost(id);

        return "redirect:/view-all-my-answers";
    }

    @GetMapping("/view-all-my-questions")
    public String showAllMyQuestionsPage(Model model) {
        //get a list of all my question posts
        User logged_in_user = getCurrentlyLoggedInUser();
        List<Question> allMyQuestions = new ArrayList<>();
        for(Post postitr : logged_in_user.getUserListOfPosts()) {
            if(postitr instanceof Question) {
                allMyQuestions.add((Question) postitr);
            }
        }

        //pass the list of all my question
        model.addAttribute("allMyQuestions", allMyQuestions);

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "view-all-my-asked-questions";
    }

    @GetMapping("/view-all-my-answers")
    public String showAllDiscussionWithMyAnswer(Model model) {
        //get a list of all my answer posts
        User logged_in_user = getCurrentlyLoggedInUser();
        List<Answer> allMyAnswers = new ArrayList<>();
        for(Post postitr : logged_in_user.getUserListOfPosts()) {
            if(postitr instanceof Answer) {
                allMyAnswers.add((Answer) postitr);
            }
        }

        //get a second list of the discussions with my answer in it
        List<Question> allQuestionsWithMyAnswer = new ArrayList<>();
        for(Answer answeritr : allMyAnswers) {
            Discussion discussionWithTheAnswer = answeritr.getPosted_in_discussion();
            Question questionOfDiscussion = getQuestionOfDiscussion(discussionWithTheAnswer);
            if(!allQuestionsWithMyAnswer.contains(questionOfDiscussion)) {
                allQuestionsWithMyAnswer.add(questionOfDiscussion);
            }
        }

        //pass the list of all my discussions with answers (aka. all my answer posts)
        model.addAttribute("allMyAnswers", allMyAnswers);

        //pass second list with all the discussion with my answer in it
        model.addAttribute("allQuestionsWithMyAnswer", allQuestionsWithMyAnswer);

        //for showing the name in the navbar only
        model.addAttribute("logged_in_user", getCurrentlyLoggedInUser());

        return "view-all-discussions-with-my-answer";
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
        for(Post postir : postService.getAll()) {
            if (counter <= amount) {
                if (postir instanceof Question) {
                    latest_posts.add(postir);
                    counter++;
                }
            } else {
                //stop and step out of the for loop
                break;
            }
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
                        Discussion discussion_with_my_answer = postitr.getPosted_in_discussion();

                        //get question of discussion
                        Question questionOfDiscussion = getQuestionOfDiscussion(discussion_with_my_answer);

                        //check if the question hasn't already been added to list -> if not then add it
                        if(!discussions_with_my_answer.contains(questionOfDiscussion)) {
                            discussions_with_my_answer.add(questionOfDiscussion);
                            counter2++;
                        }
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

    private Question getQuestionOfDiscussion(Discussion discussionWithTheAnswer) {
        Question questionOfDiscussion = new Question();
        for(Post postitr : discussionWithTheAnswer.getDiscussionListOfPosts()) {
            if(postitr instanceof Question) {
                questionOfDiscussion = (Question) postitr;
                break;
            }
        }
        return questionOfDiscussion;
    }

    private void upvotePost(long id) {
        //get the post object by id
        Post helpful_post = postService.getById((int)id);

        //first we check if the user already downvoted the post. if yes: we remove it from downvote list and add it here
        if(helpful_post.getDownvoted_from_users().contains(getCurrentlyLoggedInUser())) {
            helpful_post.getUpvoted_from_users().remove(getCurrentlyLoggedInUser());
            getCurrentlyLoggedInUser().getDownvoted_posts().remove(helpful_post);
            postService.update(id, helpful_post);
            return;
        } else {
            //check if the user already upvoted the post and tries to upvote again
            if(helpful_post.getUpvoted_from_users().contains(getCurrentlyLoggedInUser())) {
                return;
            }
        }

        //add the logged in user to the list of upvotes of the post (can be answer or question)
        helpful_post.getUpvoted_from_users().add(getCurrentlyLoggedInUser());

        //add the post to list of upvoted posts from a user
        getCurrentlyLoggedInUser().getUpvoted_posts().add(helpful_post);

        //update post in the database with the new upvote
        postService.update(id, helpful_post);
    }

    private void downvotePost(long id) {
        //get the post object by id
        Post not_helpful_post = postService.getById((int)id);

        //first we check if the user already downvoted the post. if yes: we remove it from downvote list and add it here
        if(not_helpful_post.getUpvoted_from_users().contains(getCurrentlyLoggedInUser())) {
            not_helpful_post.getDownvoted_from_users().remove(getCurrentlyLoggedInUser());
            getCurrentlyLoggedInUser().getUpvoted_posts().remove(not_helpful_post);
            postService.update(id, not_helpful_post);
            return;
        } else {
            //check if the user already downvoted the post and tries to downvote again
            if(not_helpful_post.getDownvoted_from_users().contains(getCurrentlyLoggedInUser())) {
                return;
            }
        }

        //add the logged in user to the list of downvotes of the post (can be answer or question)
        not_helpful_post.getDownvoted_from_users().add(getCurrentlyLoggedInUser());

        //add the post to list of upvoted posts from a user
        getCurrentlyLoggedInUser().getDownvoted_posts().add(not_helpful_post);

        //update post in the database with new downvote
        postService.update(id, not_helpful_post);
    }
}
