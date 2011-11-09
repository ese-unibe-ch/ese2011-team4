import java.util.List;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        // Check if the database is empty and play is running in DEV mode
        if(Play.mode == Play.Mode.DEV && User.count() == 0) {
        	Logger.info("Loading intial data");
            Fixtures.loadModels("initial-data.yml");
            
            // YAML can't load enum
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
}