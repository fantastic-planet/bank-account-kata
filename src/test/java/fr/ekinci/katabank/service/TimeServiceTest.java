package fr.ekinci.katabank.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TimeServiceTest {

	@Test
	void currentLocalDateTime_nominal() {
		// GIVEN
		TimeService timeService = new TimeService();

		// WHEN
		LocalDateTime actual = timeService.currentLocalDateTime();

		// THEN
		assertNotNull(actual);
	}
}
