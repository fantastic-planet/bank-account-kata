package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.AccountType;
import fr.ekinci.katabank.model.WithdrawalTransaction;
import fr.ekinci.katabank.repository.AccountRepository;
import fr.ekinci.katabank.security.UserService;
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
import static fr.ekinci.katabank.TestUtils.*;
import static fr.ekinci.katabank.model.AccountType.CURRENT_ACCOUNT;
import static org.mockito.Mockito.doReturn;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "36000")
class WithdrawalControllerTest {

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
	void withdrawal_nominal() {
		// GIVEN
		String url = "/withdrawal";
		createAccount(accountRepository, MOCK_USER_ID, MOCK_ACCOUNT_ID, 100);
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		WithdrawalTransaction transaction = new WithdrawalTransaction();
		transaction.setAmount(-100);
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
	void withdrawal_error_when_account_does_not_exist() {
		// GIVEN
		String url = "/withdrawal";
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		WithdrawalTransaction transaction = new WithdrawalTransaction();
		transaction.setAmount(-100);
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
	@ValueSource(longs = {0, 1, Long.MAX_VALUE})
	void withdrawal_error_when_amount_is_less_than_1(long amountValue) {
		// GIVEN
		WithdrawalTransaction transaction = new WithdrawalTransaction();
		transaction.setType(CURRENT_ACCOUNT);
		transaction.setAmount(amountValue);

		// WHEN
		webTestClient.post().uri("/withdrawal" )
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isBadRequest();
	}

	@Test
	void withdrawal_error_when_account_type_is_not_set() {
		// GIVEN
		String url = "/withdrawal";
		WithdrawalTransaction transaction = new WithdrawalTransaction();
		transaction.setType(null);
		transaction.setAmount(-100);

		// WHEN
		webTestClient.post().uri(url)
			.body(Mono.just(transaction), transaction.getClass())
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus()
			.isBadRequest();
	}
}
