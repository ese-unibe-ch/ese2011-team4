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

public class RepeatingTypeTest extends UnitTest {
	
	@Test
	public void parseFromString() {
		assertEquals(RepeatingType.NONE, RepeatingType.parseFromString("NONE"));
		assertEquals(RepeatingType.DAILY, RepeatingType.parseFromString("DAILY"));
		assertEquals(RepeatingType.WEEKLY, RepeatingType.parseFromString("weekly"));
		assertEquals(RepeatingType.MONTHLY, RepeatingType.parseFromString("MonThLy"));
		assertEquals(RepeatingType.YEARLY, RepeatingType.parseFromString("YEARLY"));
		assertNull(RepeatingType.parseFromString("DAILYY"));
		assertNull(RepeatingType.parseFromString("daaily"));
		assertNull(RepeatingType.parseFromString(""));
	}
}