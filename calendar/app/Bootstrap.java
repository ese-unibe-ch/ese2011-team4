import java.util.List;

import javax.persistence.Query;

import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        // Check if the database is empty and play is running in DEV mode
        if(Play.mode == Play.Mode.DEV && User.count() == 0) {
        	Logger.info("Loading intial data...");
            Fixtures.loadModels("initial-data.yml");
            Logger.info("Loaded the following data:");
            Logger.info("Users:\t"+User.count());
            Logger.info("Calendars:\t"+Calendar.count());
            Logger.info("Events:\t"+Event.count());
            Logger.info("Comments:\t"+Comment.count());
            Logger.info("Locations:\t"+Location.count());
            Logger.info("Messages:\t"+Message.count());
            
            // create message boxes for all users
            createMessageBoxes();
            Logger.info("Created message boxes ("+MessageBox.count()+")");
            
            // YAML can't load enum
            loadEnums();
            Logger.info("Loaded enums");
        }
    }
    
    private void createMessageBoxes() {
    	List<User> users = User.all().fetch();
    	for(User u : users) {
    		if(u.messageBox == null) {
    			u.messageBox = new MessageBox(u).save();
    			u.save();
    			
    			Query query = JPA.em().createQuery("SELECT msg FROM Message msg " +
    					"WHERE msg.recipient = ?1");
    			query.setParameter(1, u);
    			List<Message> messages = query.getResultList();
    			for(Message msg: messages) {
    				msg.inbox = u.messageBox;
    				u.messageBox.inbox.add(msg);
    				if(!msg.read)
    					u.messageBox.unreadMessages++;
    				msg.save();
    				u.messageBox.save();
    			}
    		}
    	}
    }
    
    private void loadEnums() {
        for(Event e : SingleEvent.all().<SingleEvent>fetch()) {
        	e.type = RepeatingType.NONE;
        	e.save();
        }
        
        EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		event.save();
		
		event = EventSeries.find("byName", "Breakfast at Tiffany's").first();
		event.type = RepeatingType.DAILY;
		event.save();
    }
}