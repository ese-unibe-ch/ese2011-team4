import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import models.*;

import play.test.Fixtures;
import play.test.UnitTest;

public class FullTest extends UnitTest {
	DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void fullTest() throws ParseException {
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
	 
	    // Find the next event
	    Event nextEvent = Event.find("order by startDate asc").first();
	    assertNotNull(nextEvent);
	    assertEquals("Meet Lynn Bracken", nextEvent.name);
	    assertEquals(dateFormat.parse("11.09.1953 13:00"), nextEvent.startDate);
	 
	    // Add a new event
	    Event e = new Event(jacksCalendars.get(0));
	    e.name = "Date with Lynn";
	    e.startDate = dateFormat.parse("13.09.1953 21:00");
	    e.endDate = dateFormat.parse("13.09.1953 23:00");
	    
	    assertTrue(e.validateAndSave());
	    
	    assertEquals(5, jacksCalendars.get(0).events.size());
	    assertEquals(8, Event.count());
	}
}
