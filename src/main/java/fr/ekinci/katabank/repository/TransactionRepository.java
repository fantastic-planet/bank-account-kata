package fr.ekinci.katabank.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * https://docs.spring.io/spring-data/r2dbc/docs/1.0.x/reference/html/#r2dbc.repositories
 * https://www.baeldung.com/spring-data-r2dbc
 */
public interface TransactionRepository extends ReactiveCrudRepository<TransactionEntity, String> {

	/**
	 * JPQL-style: SELECT t FROM TransactionEntity t WHERE t.userIdTo = ?1
	 */
	@Query("SELECT * FROM transactions WHERE user_id_to = $1")
	Flux<TransactionEntity> findByUserId(String userId);
}
