package fr.ekinci.katabank.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionIdGeneratorTest {

	@Test
	void generate_nominal() {
		// GIVEN
		TransactionIdGenerator tig = new TransactionIdGenerator();

		// WHEN
		String actual = tig.generate();

		// THEN
		assertNotNull(actual);
		assertTrue(actual.length() > 32);
	}
}