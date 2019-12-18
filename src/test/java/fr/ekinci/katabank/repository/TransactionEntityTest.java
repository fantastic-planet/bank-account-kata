package fr.ekinci.katabank.repository;

import fr.ekinci.katabank.TestUtils;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionEntityTest {

	@Autowired
	private TransactionRepository tr;

	@Autowired
	private ConnectionFactory cf;

	@BeforeEach
	void setUp() {
		TestUtils.createTableTransactions(cf);
	}

	@AfterEach
	void tearDown() {
		TestUtils.dropTableTransactions(cf);
	}

	@Test
	void insert_nominal() {
		// GIVEN
		TransactionEntity te = transactionEntity();

		// WHEN
		TransactionEntity actual = tr.save(te).block();

		// THEN
		assertEquals(1, tr.count().block());
		assertEquals("MOCK_ID", actual.getId());
		assertEquals("MOCK_USER_ID", actual.getUserIdFrom());
		assertNotNull(actual.getDate());
		assertEquals(123L, actual.getAmount());
	}

	@Test
	void update_nominal() {
		// GIVEN
		tr.save(transactionEntity()).block();
		TransactionEntity te = new TransactionEntity(false);
		te.setId("MOCK_ID");
		te.setUserIdFrom("MOCK_USER_ID");
		te.setDate(LocalDateTime.now());
		te.setAmount(789L);

		// WHEN
		TransactionEntity actual = tr.save(te).block();

		// THEN
		assertEquals(1, tr.count().block());
		assertEquals("MOCK_ID", actual.getId());
		assertEquals("MOCK_USER_ID", actual.getUserIdFrom());
		assertNotNull(actual.getDate());
		assertEquals(789L, actual.getAmount());
	}

	@Test
	void isNew() {
		// GIVEN
		TransactionEntity te = transactionEntity();

		// WHEN
		boolean isNew = te.isNew();

		// THEN
		assertTrue(isNew);
	}

	private TransactionEntity transactionEntity() {
		TransactionEntity te = new TransactionEntity(true);
		te.setId("MOCK_ID");
		te.setUserIdFrom("MOCK_USER_ID");
		te.setDate(LocalDateTime.now());
		te.setAmount(123L);
		return te;
	}
}