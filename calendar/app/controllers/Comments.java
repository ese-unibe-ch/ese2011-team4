package controllers;

import play.mvc.Controller;
import play.mvc.With;
import models.Comment;
import models.SingleEvent;
import models.User;

@With(Secure.class)
public class Comments extends Controller {
    public static void add(Long id){
    	SingleEvent event = SingleEvent.findById(id);
    	User connectedUser = User.find("email", Security.connected()).first();
        render(event, connectedUser);
    }
	
    public static void delete(Long eventId, Long commentId){
    	SingleEvent event = SingleEvent.findById(eventId);
    	Comment comment = Comment.findById(commentId);
    	assert event != null;
    	assert comment != null;
    	
    	comment.delete();
    	show(event.id);
    }
    
    public static void show(Long id){
    	SingleEvent event = SingleEvent.findById(id);
    	User connectedUser = User.find("email", Security.connected()).first();
    	render(event, connectedUser);
    }

    public static void update(Long id, Long userId, String content ) {
    	SingleEvent event = SingleEvent.findById(id);
    	assert event != null;
    	
    	User author = User.findById(userId);
    	assert author != null;
    	
    	Comment comment = new Comment(author.fullname, event);
    	comment.content = content;
 
    	if(comment.validateAndSave()) {
    		flash.success("Thanks for posting %s", author);
    		show(event.id);
    	} else {
    		params.flash();
    		validation.keep();
			add(event.id);
    	}
    }
}
