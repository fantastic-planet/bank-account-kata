package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.AccountType;
import fr.ekinci.katabank.model.DepositTransaction;
import fr.ekinci.katabank.repository.AccountRepository;
import fr.ekinci.katabank.security.UserService;
import fr.ekinci.katabank.service.TimeService;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static fr.ekinci.katabank.model.AccountType.CURRENT_ACCOUNT;
import static org.mockito.Mockito.doReturn;
import static fr.ekinci.katabank.TestUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
class DepositControllerTest {

	private static final String MOCK_USER_ID = "MOCK_USER_ID";
	private static final String MOCK_ACCOUNT_ID = MOCK_USER_ID + ":" + CURRENT_ACCOUNT;

	@BeforeEach
	void beforeEach() {
		createTableTransactions(cf);
		createTableAccounts(cf);
	}

	@AfterEach
	void tearDown() {
		dropTableTransactions(cf);
		dropTableAccounts(cf);
	}

	@Autowired
	private ConnectionFactory cf;

	@Autowired
	private WebTestClient webTestClient;

	@Autowired
	private AccountRepository accountRepository;

	@MockBean
	private UserService userService;

	@Test
	void deposit_nominal() {
		// GIVEN
		String url = "/deposit";
		createAccount(accountRepository, MOCK_USER_ID, MOCK_ACCOUNT_ID, 0);
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		DepositTransaction transaction = new DepositTransaction();
		transaction.setAmount(100);
		transaction.setType(AccountType.CURRENT_ACCOUNT);

		// WHEN
		webTestClient.post().uri(url)
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			// THEN
			.expectStatus()
			.isOk();
	}

	@Test
	void deposit_error_when_account_does_not_exist() {
		// GIVEN
		String url = "/deposit";
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		DepositTransaction transaction = new DepositTransaction();
		transaction.setAmount(100);
		transaction.setType(AccountType.CURRENT_ACCOUNT);

		// WHEN
		webTestClient.post().uri(url)
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			// THEN
			.expectStatus()
			.isForbidden();
	}

	@ParameterizedTest
	@ValueSource(longs = {0, -1, Long.MIN_VALUE})
	void deposit_error_when_amount_is_less_than_1(long amountValue) {
		// GIVEN
		DepositTransaction transaction = new DepositTransaction();
		transaction.setType(CURRENT_ACCOUNT);
		transaction.setAmount(amountValue);

		// WHEN
		webTestClient.post().uri("/deposit" )
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isBadRequest();
	}

	@Test
	void deposit_error_when_account_type_is_not_set() {
		// GIVEN
		String url = "/deposit";
		DepositTransaction transaction = new DepositTransaction();
		transaction.setType(null);
		transaction.setAmount(100);

		// WHEN
		webTestClient.post().uri(url)
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isBadRequest();
	}
}
