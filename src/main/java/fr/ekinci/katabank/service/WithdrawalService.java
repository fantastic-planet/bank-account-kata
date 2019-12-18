package fr.ekinci.katabank.service;

import fr.ekinci.katabank.exception.AccountDoesNotExistException;
import fr.ekinci.katabank.exception.NotEnoughMoneyException;
import fr.ekinci.katabank.model.AccountType;
import fr.ekinci.katabank.model.WithdrawalTransaction;
import fr.ekinci.katabank.repository.AccountEntity;
import fr.ekinci.katabank.repository.AccountRepository;
import fr.ekinci.katabank.repository.TransactionEntity;
import fr.ekinci.katabank.repository.TransactionRepository;
import fr.ekinci.katabank.security.User;
import fr.ekinci.katabank.security.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class WithdrawalService {
	private final UserService userService;
	private final TransactionRepository transactionRepository;
	private final TransactionIdGenerator transactionIdGenerator;
	private final AccountRepository accountRepository;
	private final TimeService timeService;

	public WithdrawalService(TransactionRepository transactionRepository, UserService userService,
		TransactionIdGenerator transactionIdGenerator, AccountRepository accountRepository,
		TimeService timeService) {
		this.transactionRepository = transactionRepository;
		this.userService = userService;
		this.transactionIdGenerator = transactionIdGenerator;
		this.accountRepository = accountRepository;
		this.timeService = timeService;
	}

	@Transactional
	public Mono<Void> withdrawal(WithdrawalTransaction transaction) {
		Mono<User> userMono = userService.getUser();

		Mono<AccountEntity> accountEntityMono = userMono.map(user -> accountId(user, transaction.getType()))
			.flatMap(accountRepository::findById)
			.switchIfEmpty(Mono.error(new AccountDoesNotExistException()))
			.filter(accountEntity -> hasEnoughMoneyToWithdrawal(accountEntity, transaction.getAmount()))
			.switchIfEmpty(Mono.error(new NotEnoughMoneyException()))
			.map(accountEntity -> updateAccountEntity(accountEntity, transaction))
			.flatMap(accountRepository::save);

		Mono<TransactionEntity> transactionEntityMono = userMono.map(user -> getTransactionEntity(user, transaction.getAmount()))
			.flatMap(transactionRepository::save);

		return Mono.when(accountEntityMono, transactionEntityMono);
	}

	private String accountId(User user, AccountType type) {
		return user.getPrincipal() + ":" + type;
	}

	private AccountEntity updateAccountEntity(AccountEntity accountEntity, WithdrawalTransaction transaction) {
		accountEntity.setAmount(accountEntity.getAmount() + transaction.getAmount());
		return accountEntity;
	}

	private TransactionEntity getTransactionEntity(User user, long amount) {
		TransactionEntity te = new TransactionEntity(true);
		te.setId(transactionIdGenerator.generate());
		te.setUserIdFrom(user.getPrincipal());
		te.setUserIdTo(user.getPrincipal());
		te.setDate(timeService.currentLocalDateTime());
		te.setAmount(amount);
		return te;
	}

	private boolean hasEnoughMoneyToWithdrawal(AccountEntity accountEntity, long amount) {
		return accountEntity.getAmount() + amount >= 0;
	}

}
