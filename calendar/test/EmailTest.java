import java.util.List;

import javax.persistence.Query;

import models.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import play.Logger;
import play.data.validation.Validation;
import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

public class EmailTest extends UnitTest {
	
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
		assertEquals(8, SingleEvent.count());
	}
	
	@After
	public void deleteDataBase() {
		Fixtures.deleteDatabase();
		assertEquals(0, SingleEvent.count());
	}
	
	@Test
	public void create() {
		Email email = new Email();
		String[] array = {"test", "123"};
		email.setBcc(array);
		email.setCc(array);
		email.setFrom("test");
		email.setMimeType("test");
		email.setSubject("test");
		email.setText("test");
		email.setTo(array);
		
		assertArrayEquals(array, email.getBcc());
		assertArrayEquals(array, email.getCc());
		assertEquals("test", email.getFrom());
		assertEquals("test", email.getMimeType());
		assertEquals("test", email.getSubject());
		assertEquals("test", email.getText());
		assertArrayEquals(array, email.getTo());
	}
}