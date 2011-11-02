import models.*;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.test.Fixtures;
import play.test.UnitTest;


public class LocationTest extends UnitTest {
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
		Fixtures.loadModels("initial-data.yml");
		assertEquals(1, Location.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, SingleEvent.count());
	}
	
	@Test
	public void eventWithLocation() {
		// Get an event
		SingleEvent e1 = SingleEvent.find("byName", "Meet Lynn Bracken").first();
		
		// Test if it has an event
		assertEquals("Bernstrasse", e1.location.street);
	}
	
	@Test
	public void addLocation() {
		// Get an Event
		SingleEvent e0 = SingleEvent.find("byName", "Collections").first();
		
		// Create a new Location
		Location location = new Location();
		location.street = "Worbstrasse";
		location.num = "14";
		location.city = "Gümligen";
		location.pincode = "3000";
		location.country = "Switzerland";
		
		// Save it
		assertTrue(location.validateAndSave());
		
		// Add it to the event
		assertNull(e0.location);
		e0.location = location;
		
		// Save it
		e0.validateAndSave();
		for(play.data.validation.Error e : Validation.errors())
			Logger.info(e.message());
		
		assertTrue(e0.validateAndSave());
		
		// Retrieve data
		location = Location.findById(location.id);
		assertEquals("Worbstrasse", location.street);
		assertEquals("14", location.num);
		assertEquals("Gümligen", location.city);
		assertEquals("3000", location.pincode);
		assertEquals("Switzerland", location.country);
	}
}