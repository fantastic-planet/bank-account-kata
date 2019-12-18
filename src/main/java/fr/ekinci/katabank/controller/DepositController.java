package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.DepositTransaction;
import fr.ekinci.katabank.service.DepositService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import javax.validation.Valid;

@RestController
class DepositController {
	private final DepositService depositService;

	DepositController(DepositService depositService) {
		this.depositService = depositService;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/deposit")
	Mono<Void> deposit(@Valid @RequestBody DepositTransaction transaction) {
		return depositService.deposit(transaction);
	}
}
