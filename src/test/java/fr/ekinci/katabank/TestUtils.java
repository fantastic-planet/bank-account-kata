package fr.ekinci.katabank;

import fr.ekinci.katabank.repository.AccountEntity;
import fr.ekinci.katabank.repository.AccountRepository;
import fr.ekinci.katabank.security.User;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.data.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static fr.ekinci.katabank.model.AccountType.CURRENT_ACCOUNT;

public class TestUtils {
	private static final String CREATE_TABLE_TRANSACTIONS = "CREATE TABLE transactions(id VARCHAR, user_id_from VARCHAR, user_id_to VARCHAR, date TIMESTAMP, amount INTEGER)";
	private static final String DROP_TABLE_TRANSACTIONS = "DROP TABLE transactions";
	private static final String CREATE_TABLE_ACCOUNTS = "CREATE TABLE accounts(id VARCHAR, user_id VARCHAR, type VARCHAR, amount BIGINT)";
	private static final String DROP_TABLE_ACCOUNTS = "DROP TABLE accounts";

	public static void createTableTransactions(ConnectionFactory cf) {
		execute(cf, CREATE_TABLE_TRANSACTIONS);
	}

	public static void dropTableTransactions(ConnectionFactory cf) {
		execute(cf, DROP_TABLE_TRANSACTIONS);
	}

	public static void createTableAccounts(ConnectionFactory cf) {
		execute(cf, CREATE_TABLE_ACCOUNTS);
	}

	public static void dropTableAccounts(ConnectionFactory cf) {
		execute(cf, DROP_TABLE_ACCOUNTS);
	}

	private static void execute(ConnectionFactory cf, String statement) {
		// https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#reference
		DatabaseClient client = DatabaseClient.create(cf);
		client.execute(statement)
				.fetch()
				.rowsUpdated()
				.as(StepVerifier::create)
				.expectNextCount(1)
				.verifyComplete();
	}

	public static void createAccount(AccountRepository accountRepository, String userId, String accountId, long initialAmount) {
		AccountEntity accountEntity = new AccountEntity(true);
		accountEntity.setId(accountId);
		accountEntity.setUserId(userId);
		accountEntity.setAmount(initialAmount);
		accountEntity.setType(CURRENT_ACCOUNT);
		accountRepository.save(accountEntity).block();
	}

	public static Mono<User> mockUser(String userId) {
		User user = new User();
		user.setPrincipal(userId);
		return Mono.just(user);
	}
}
