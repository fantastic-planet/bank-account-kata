package fr.ekinci.katabank.controller;

import fr.ekinci.katabank.model.DepositTransaction;
import fr.ekinci.katabank.model.WithdrawalTransaction;
import fr.ekinci.katabank.service.DepositService;
import fr.ekinci.katabank.service.WithdrawalService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
class WithdrawalController {
	private final WithdrawalService withdrawalService;

	WithdrawalController(WithdrawalService withdrawalService) {
		this.withdrawalService = withdrawalService;
	}

	@RequestMapping(method = RequestMethod.POST, path = "/withdrawal")
	Mono<Void> withdrawal(@Valid @RequestBody WithdrawalTransaction transaction) {
		return withdrawalService.withdrawal(transaction);
	}
}
