package fr.ekinci.katabank.repository;

import fr.ekinci.katabank.TestUtils;
import fr.ekinci.katabank.model.AccountType;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class AccountEntityTest {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private ConnectionFactory cf;

	@BeforeEach
	void setUp() {
		TestUtils.createTableAccounts(cf);
	}

	@AfterEach
	void tearDown() {
		TestUtils.dropTableAccounts(cf);
	}

	@Test
	void insert_nominal() {
		// GIVEN
		AccountEntity accountEntity = accountEntity();

		// WHEN
		AccountEntity actual = accountRepository.save(accountEntity).block();

		// THEN
		assertEquals(1, accountRepository.count().block());
		assertEquals("userId:" + AccountType.CURRENT_ACCOUNT, actual.getId());
		assertEquals("userId", actual.getUserId());
		assertEquals(AccountType.CURRENT_ACCOUNT, actual.getType());
		assertEquals(12_300, actual.getAmount());
	}

	@Test
	void update_nominal() {
		// GIVEN
		accountRepository.save(accountEntity()).block();
		AccountEntity accountEntity = new AccountEntity(false);
		accountEntity.setId("userId:" + AccountType.CURRENT_ACCOUNT);
		accountEntity.setUserId("userId");
		accountEntity.setType(AccountType.CURRENT_ACCOUNT);
		accountEntity.setAmount(78_900L);

		// WHEN
		AccountEntity actual = accountRepository.save(accountEntity).block();

		// THEN
		assertEquals(1, accountRepository.count().block());
		assertEquals("userId:" + AccountType.CURRENT_ACCOUNT, actual.getId());
		assertEquals("userId", actual.getUserId());
		assertEquals(AccountType.CURRENT_ACCOUNT, actual.getType());
		assertEquals(78_900L, actual.getAmount());
	}

	@Test
	void isNew() {
		// GIVEN
		AccountEntity accountEntity = accountEntity();

		// WHEN
		boolean isNew = accountEntity.isNew();

		// THEN
		assertTrue(isNew);
	}

	private AccountEntity accountEntity() {
		AccountEntity accountEntity = new AccountEntity(true);
		accountEntity.setId("userId:" + AccountType.CURRENT_ACCOUNT);
		accountEntity.setUserId("userId");
		accountEntity.setType(AccountType.CURRENT_ACCOUNT);
		accountEntity.setAmount(12_300L);
		return accountEntity;
	}
}