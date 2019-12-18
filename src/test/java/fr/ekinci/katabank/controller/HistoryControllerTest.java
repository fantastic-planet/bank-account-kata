package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.AccountType;
import fr.ekinci.katabank.model.DepositTransaction;
import fr.ekinci.katabank.repository.TransactionEntity;
import fr.ekinci.katabank.repository.TransactionRepository;
import fr.ekinci.katabank.security.UserService;
import fr.ekinci.katabank.service.TimeService;
import io.r2dbc.spi.ConnectionFactory;
import org.h2.util.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import static fr.ekinci.katabank.TestUtils.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
class HistoryControllerTest {
	private static final String MOCK_USER_ID = "MOCK_USER_ID";

	@Autowired
	private ConnectionFactory cf;

	@Value("classpath:history.json")
	private Resource resource;

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private UserService userService;

	@Autowired
	private TransactionRepository transactionRepository;

	@MockBean
	private TimeService timeService;

	@BeforeEach
	void beforeEach() {
		createTableTransactions(cf);
	}

	@AfterEach
	void tearDown() {
		dropTableTransactions(cf);
	}

	@Test
	void getHistory_nominal() throws IOException {
		// GIVEN
		doReturn(LocalDateTime.of(1970, 1, 1, 0, 0)).when(timeService).currentLocalDateTime();
		String jsonResponse = new String(IOUtils.readBytesAndClose(resource.getInputStream(), -1), StandardCharsets.UTF_8);
		insertTransactionEntries();
		String url = "/history";
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		DepositTransaction transaction = new DepositTransaction();
		transaction.setAmount(100);
		transaction.setType(AccountType.CURRENT_ACCOUNT);

		// WHEN
		webTestClient.get().uri(url)
			.exchange()
			// THEN
			.expectStatus()
			.isOk()
			.expectBody().json(jsonResponse);
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
		te.setDate(timeService.currentLocalDateTime());
		te.setAmount(200L);
		return te;
	}
}
