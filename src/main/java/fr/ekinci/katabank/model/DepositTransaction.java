package fr.ekinci.katabank.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class DepositTransaction {
	@Min(100)
	private long amount;

	@NotNull
	private AccountType type;
}
