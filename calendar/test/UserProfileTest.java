import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

import play.test.Fixtures;
import play.test.UnitTest;

public class UserProfileTest extends UnitTest{
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
        // YAML can't load enum
        for(Event e : SingleEvent.all().<SingleEvent>fetch()) {
        	e.type = RepeatingType.NONE;
        	e.save();
        }
        
        EventSeries event = EventSeries.find("byName", "Weekly Meeting").first();
		event.type = RepeatingType.WEEKLY;
		event.save();
		assertEquals(3, User.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, User.count());
	}
	
	@Test
	public void create() {
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		DateTime birthday = new DateTime();
		
		// Create a new user profile
		UserProfile uprof = new UserProfile("test@test.com", "Test User", "Male", birthday, loc);
		
		assertEquals("test@test.com", uprof.useremail);
		assertEquals("Test User", uprof.name);
		assertEquals("Male", uprof.gender);
		assertEquals(birthday, uprof.birthday);
		assertEquals(loc, uprof.address);
		assertEquals("test@test.com", uprof.toString());
	}
}