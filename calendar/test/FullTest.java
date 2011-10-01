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
	public void fullTest() throws InvalidEventException, ParseException {
	    Fixtures.loadModels("data.yml");
	 
	    // Count things
	    assertEquals(2, User.count());
	    assertEquals(3, Calendar.count());
	    assertEquals(4, Event.count());
	 
	    // Try to connect as users
	    assertNotNull(User.connect("jack.vincennes@lapolice.com", "secret"));
	    assertNotNull(User.connect("bud.white@lapolice.com", "secret"));
	    assertNull(User.connect("bud.white@lapolice.com", "notsosecret"));
	    assertNull(User.connect("sid.hudgens@hollywood.com", "secret"));
	 
	    // Find all of Jack's calendars
	    List<Calendar> jacksCalendars = Calendar.find("owner.email", "jack.vincennes@lapolice.com").fetch();
	    assertEquals(2, jacksCalendars.size());
	 
	    // Find all of Jack's events
	    List<Event> jackEvents = Event.find("calendar.owner.email", "jack.vincennes@lapolice.com").fetch();
	    assertEquals(3, jackEvents.size());
	 
	    // Find the next event
	    Event nextEvent = Event.find("order by startDate asc").first();
	    assertNotNull(nextEvent);
	    assertEquals("Meet Lynn Bracken", nextEvent.name);
	 
	    // Add a new event
	    jacksCalendars.get(0).addEvent("Cinema", dateFormat.parse("13.09.1953 21:00"), dateFormat.parse("13.09.1953 23:00"), false);
	    assertEquals(3, jacksCalendars.get(0).events.size());
	    assertEquals(5, Event.count());
	}
}
