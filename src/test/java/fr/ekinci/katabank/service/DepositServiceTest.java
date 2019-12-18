package fr.ekinci.katabank.service;

import fr.ekinci.katabank.exception.AccountDoesNotExistException;
import fr.ekinci.katabank.model.DepositTransaction;
import fr.ekinci.katabank.repository.AccountEntity;
import fr.ekinci.katabank.repository.AccountRepository;
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
import static fr.ekinci.katabank.model.AccountType.CURRENT_ACCOUNT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static fr.ekinci.katabank.TestUtils.*;

@SpringBootTest
class DepositServiceTest {

	private static final String MOCK_USER_ID = "MOCK_USER_ID";
	private static final String MOCK_TRANSACTION_ID = "MOCK_TRANSACTION_ID";
	private static final String MOCK_ACCOUNT_ID = MOCK_USER_ID + ":" + CURRENT_ACCOUNT;

	@MockBean
	private UserService userService;

	@MockBean
	private TransactionIdGenerator transactionIdGenerator;

	@Autowired
	private DepositService depositService;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ConnectionFactory cf;

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

	@Test
	void deposit_nominal() {
		// GIVEN
		createAccount(accountRepository, MOCK_USER_ID, MOCK_ACCOUNT_ID, 2_000);
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		doReturn(MOCK_TRANSACTION_ID).when(transactionIdGenerator).generate();
		DepositTransaction transaction = new DepositTransaction();
		transaction.setType(CURRENT_ACCOUNT);
		transaction.setAmount(12_300);

		// WHEN
		depositService.deposit(transaction).block();

		// THEN
		TransactionEntity transactionEntity = transactionRepository.findById(MOCK_TRANSACTION_ID).block();
		assertNotNull(transactionEntity);
		assertEquals(MOCK_TRANSACTION_ID, transactionEntity.getId());
		assertEquals(MOCK_USER_ID, transactionEntity.getUserIdFrom());
		assertEquals(MOCK_USER_ID, transactionEntity.getUserIdTo());
		assertNotNull(transactionEntity.getDate());
		assertEquals(12_300, transactionEntity.getAmount());

		AccountEntity accountEntity = accountRepository.findById(MOCK_ACCOUNT_ID).block();
		assertNotNull(accountEntity);
		assertEquals(MOCK_ACCOUNT_ID, accountEntity.getId());
		assertEquals(MOCK_USER_ID, accountEntity.getUserId());
		assertEquals(CURRENT_ACCOUNT, accountEntity.getType());
		assertEquals(14_300, accountEntity.getAmount());
	}

	@Test
	void deposit_error_when_account_does_not_exist() {
		// GIVEN
		doReturn(mockUser(MOCK_USER_ID)).when(userService).getUser();
		DepositTransaction transaction = new DepositTransaction();
		transaction.setAmount(12_300);

		// WHEN
		assertThrows(
			AccountDoesNotExistException.class,
			() -> depositService.deposit(transaction).block()
		);
	}
}
