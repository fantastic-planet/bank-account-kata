package fr.ekinci.katabank.model;

import fr.ekinci.katabank.repository.TransactionEntity;
import lombok.Data;
import java.util.List;

@Data
public class History {
	private Long balance;
	private List<TransactionEntity> operations;
}
