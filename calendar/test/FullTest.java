import java.util.List;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import models.*;

import play.test.Fixtures;
import play.test.UnitTest;

public class FullTest extends UnitTest {
	private static DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void fullTest() {
	    Fixtures.loadModels("data.yml");
	 
	    // Count things
	    assertEquals(3, User.count());
	    assertEquals(4, Calendar.count());
	    assertEquals(7, Event.count());
	 
	    // Try to connect as users
	    assertNotNull(User.connect("jack.vincennes@lapd.com", "secret"));
	    assertNotNull(User.connect("bud.white@lapd.com", "secret"));
	    assertNull(User.connect("bud.white@lapd.com", "notsosecret"));
	    assertNull(User.connect("sid.hudgens@hollywood.com", "secret"));
	 
	    // Find all of Jack's calendars
	    List<Calendar> jacksCalendars = Calendar.find("owner.email", "jack.vincennes@lapd.com").fetch();
	    assertEquals(2, jacksCalendars.size());
	 
	    // Find all of Jack's events
	    List<Event> jackEvents = Event.find("calendar.owner.email", "jack.vincennes@lapd.com").fetch();
	    assertEquals(4, jackEvents.size());
	    
	    // Add a new event
	    Event e = new Event(jacksCalendars.get(0));
	    e.name = "Date with Lynn";
	    e.startDate = format.parseDateTime("13.09.1953 21:00");
	    e.endDate = format.parseDateTime("13.09.1953 23:00");
	    
	    assertTrue(e.validateAndSave());
	    
	    assertEquals(5, jacksCalendars.get(0).events.size());
	    assertEquals(8, Event.count());
	}
}