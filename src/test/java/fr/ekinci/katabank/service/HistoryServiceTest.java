package fr.ekinci.katabank.service;

import fr.ekinci.katabank.model.History;
import fr.ekinci.katabank.repository.TransactionEntity;
import fr.ekinci.katabank.repository.TransactionRepository;
import fr.ekinci.katabank.security.UserService;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import static fr.ekinci.katabank.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class HistoryServiceTest {
	private static final String MOCK_USER_ID = "MOCK_USER_ID";

	@MockBean
	private UserService userService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private ConnectionFactory cf;

	@BeforeEach
	void beforeEach() {
		createTableTransactions(cf);
	}

	@AfterEach
	void tearDown() {
		dropTableTransactions(cf);
	}

	@Test
	void history_nominal() {
		// GIVEN
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		insertTransactionEntries();

		// WHEN
		History history = historyService.getHistory().block();

		// THEN
		assertEquals(5, transactionRepository.findAll().collectList().block().size());
		assertNotNull(history);
		assertEquals(4, history.getOperations().size());
		assertEquals(800L, history.getBalance());
	}

	private void insertTransactionEntries() {
		transactionRepository.saveAll(Arrays.asList(
			transactionEntity("MOCK_ID1", MOCK_USER_ID),
			transactionEntity("MOCK_ID2", MOCK_USER_ID),
			transactionEntity("MOCK_ID3", MOCK_USER_ID),
			transactionEntity("MOCK_ID4", MOCK_USER_ID)
		)).collectList().block();
		transactionRepository.save(transactionEntity("MOCK_ID5", "ANOTHER_USER_ID")).block();
	}

	private TransactionEntity transactionEntity(String transactionId, String id) {
		TransactionEntity te = new TransactionEntity(true);
		te.setId(transactionId);
		te.setUserIdFrom(MOCK_USER_ID);
		te.setUserIdTo(id);
		te.setDate(LocalDateTime.now());
		te.setAmount(200L);
		return te;
	}
}
