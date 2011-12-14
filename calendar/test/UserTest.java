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

public class UserTest extends UnitTest{
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
		// Create a new user
		User lynn = new User("lynn.bracken@gmail.com", "secret", "Lynn Bracken");
		
		assertTrue(lynn.validateAndSave());
		assertEquals(4, User.count());
	}
	
	@Ignore
	@Test
	public void delete() {
		// Find a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		Long id = jack.id;
		
		// Delete it
		jack.delete();
		
		// Try to find it
		assertNull(User.findById(id));
		assertEquals(2, User.count());
		
		// Count objects
		assertEquals(2, Calendar.count());
		assertEquals(4, Event.count());
		assertEquals(0, Comment.count());
	}
	
	@Test
	public void validateUniqueId() {
		// Create a new User with false email
		User mail = new User("lynn.asdhaks.com", "secret", "Test User 1");
		
		assertFalse(mail.validateAndSave());
		assertEquals(3, User.count());
		
		// Create a user with a non unique mail
		User jack = new User("jack.vincennes@lapd.com", "secret", "Test User 2");
		
		assertFalse(jack.validateAndSave());
		assertEquals(3, User.count());
	}
	
	@Test
	public void connect() {
	    assertNotNull(User.connect("jack.vincennes@lapd.com", "secret"));
	    assertNotNull(User.connect("bud.white@lapd.com", "secret"));
	    assertNull(User.connect("bud.white@lapd.com", "notsosecret"));
	    assertNull(User.connect("sid.hudgens@hollywood.com", "secret"));
	}
	
	@Test
	public void addFavorite() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		jack.addFavorite(bud);
		assertEquals(1, jack.favorites.size());
		assertTrue(jack.favorites.contains(bud));
		
		assertEquals(3, User.count());
	}
	
	@Test
	public void isFavorite() {
		User ed = User.find("byEmail", "ed.exley@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		assertTrue(ed.isFavorite(bud));
	}
	
	@Test
	public void removeFavorite() {
		User ed = User.find("byEmail", "ed.exley@lapd.com").first();
		User bud = User.find("byEmail", "bud.white@lapd.com").first();
		
		ed.removeFavorite(bud);
		assertFalse(ed.isFavorite(bud));
		
		assertEquals(3, User.count());
	}
	
	
	@Test
	public void GetAndSetProfileEntries() {
		// Get a user
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		// Get location
		Location loc = Location.find("Bernstrasse", "1", "Bern", "Switzerland", "3000");
		
		DateTime day = new DateTime();
		
		// Reset values
		jack.address = null;
		jack.birthday = null;
		jack.descriptionUser = "";
		jack.gender = "";
		jack.nickname = "";
		jack.telephone = "";
		
		// Set values
		jack.setAddress(loc);
		jack.setAddressVisibility(true);
		jack.setBirthday(day);
		jack.setBirthdayVisibility(true);
		jack.setDescripton("abc");
		jack.setGender("male");
		jack.setGenderVisibility(true);
		jack.setNickName("Jacky");
		jack.setTelephone("12345");
		jack.setTelephoneVisibility(true);
		
		// Get values
		Location address = jack.getAddress();
		boolean addressVisibility = jack.getAddressVisibility();
		DateTime birthday = jack.getBirthday();
		boolean birthdayVisibility = jack.getBirthdayVisibility();
		String description = jack.getDescription();
		String gender = jack.getGender();
		boolean genderVisibility = jack.getGenderVisibility();
		String nickName = jack.getNickName();
		String telephone = jack.getTelephone();
		boolean telephoneVisibility = jack.getTelephoneVisibility();
		
		assertEquals(loc, address);
		assertTrue(addressVisibility);
		assertEquals(day, birthday);
		assertTrue(birthdayVisibility);
		assertEquals("abc", description);
		assertEquals("male", gender);
		assertTrue(genderVisibility);
		assertEquals("Jacky", nickName);
		assertEquals("12345", telephone);
		assertTrue(telephoneVisibility);		
	}
	
	@Test
	public void get_setDefaultCalendar() {
		User jack = User.find("byEmail", "jack.vincennes@lapd.com").first();
		
		Calendar calendar = Calendar.find("byName", "Jacks Agenda").first();
		Calendar calendar2 = Calendar.find("byName", "Jacks Secret Agenda").first();
		
		assertEquals(calendar, jack.getDefaultCalendar());
		jack.setDefaultCalendar(calendar2);
		assertEquals(calendar2, jack.getDefaultCalendar());
	}
}