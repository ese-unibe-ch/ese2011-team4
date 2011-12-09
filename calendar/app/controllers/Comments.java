package controllers;

import play.mvc.Controller;
import play.mvc.With;
import models.*;

@With(Secure.class)
public class Comments extends Controller {
    public static void add(Long id){
    	Event event = Event.findById(id);
    	User connectedUser = User.find("email", Security.connected()).first();
        render(event, connectedUser);
    }
	
    public static void delete(Long eventId, Long commentId){
    	Event event = Event.findById(eventId);
    	Comment comment = Comment.findById(commentId);
    	assert event != null;
    	assert comment != null;
    	
    	comment.delete();
    	show(event.id);
    }
    
    public static void show(Long id){
    	Event event = Event.findById(id);
    	User connectedUser = User.find("email", Security.connected()).first();
    	render(event, connectedUser);
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
    		show(event.id);
    	} else {
    		params.flash();
    		validation.keep();
			add(event.id);
    	}
    }
}
