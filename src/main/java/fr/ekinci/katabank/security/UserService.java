package fr.ekinci.katabank.security;

import fr.ekinci.katabank.exception.UserDoesNotExistException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

	/**
	 * Call this method when the user must exist
	 */
	public Mono<User> getUser() {
		return ReactiveSecurityContextHolder.getContext()
			.map(SecurityContext::getAuthentication)
			.map(a -> (User) a)
			.switchIfEmpty(Mono.error(new UserDoesNotExistException()));
	}
}
