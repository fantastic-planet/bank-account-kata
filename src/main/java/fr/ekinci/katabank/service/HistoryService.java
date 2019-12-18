package fr.ekinci.katabank.service;

import fr.ekinci.katabank.model.History;
import fr.ekinci.katabank.repository.TransactionEntity;
import fr.ekinci.katabank.repository.TransactionRepository;
import fr.ekinci.katabank.security.User;
import fr.ekinci.katabank.security.UserService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class HistoryService {
	private final UserService userService;
	private final TransactionRepository transactionRepository;

	public HistoryService(UserService userService, TransactionRepository transactionRepository) {
		this.userService = userService;
		this.transactionRepository = transactionRepository;
	}

	public Mono<History> getHistory() {
		return userService.getUser()
			.map(User::getPrincipal)
			.flatMapMany(transactionRepository::findByUserId)
			.collectList()
			.map(this::createHistory);
	}

	private History createHistory(List<TransactionEntity> transactionEntities) {
		History history = new History();
		long balance = transactionEntities.stream()
			.mapToLong(TransactionEntity::getAmount)
			.sum();
		history.setBalance(balance);
		history.setOperations(transactionEntities);
		return history;
	}
}
