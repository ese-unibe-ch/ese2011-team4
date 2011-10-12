import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

import models.*;
 
@OnApplicationStart
public class Bootstrap extends Job {
    public void doJob() {
        // Check if the database is empty
        if(User.count() == 0) {
            Fixtures.loadModels("initial-data.yml");
            System.out.println("Loaded initial data.");
        } else {
        	System.out.println("Didn't have to load intial data.");
        }
    }
}