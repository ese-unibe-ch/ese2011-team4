package controllers;

import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Comments extends Controller {
    public static void add(Long id){
    	Event event = Event.findById(id);
        render(event);
    }
	
    public static void delete(Long eventId, Long commentId){
    	Event event = Event.findById(eventId);
    	Comment comment = Comment.findById(commentId);
    	assert event != null;
    	assert comment != null;
    	
    	comment.delete();
    	flash.success("Comment sucessfully deleted.");
    	add(event.id);
    }

    public static void update(Long eventId, String content ) {
    	Event event = Event.findById(eventId);
    	assert event != null;
    	
    	User author = Users.getConnectedUser();
    	assert author != null;
    	
    	Comment comment = new Comment(author, event);
    	comment.content = content;
 
    	if(comment.validateAndSave()) {
    		flash.success("Thanks for posting %s", author.toString());
    		add(event.id);
    	} else {
    		params.flash();
    		validation.keep();
			add(event.id);
    	}
    }
}