package fr.ekinci.katabank.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, String> {

}
