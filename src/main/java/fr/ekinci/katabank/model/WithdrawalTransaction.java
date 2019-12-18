package fr.ekinci.katabank.model;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class WithdrawalTransaction {
	@Max(-100)
	private long amount;

	@NotNull
	private AccountType type;
}
