package fr.ekinci.katabank.repository;

import fr.ekinci.katabank.model.AccountType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("accounts")
@Data
@NoArgsConstructor
public class AccountEntity implements Persistable<String> {
	// userId:type
	@Id
	private String id;

	@Column("user_id")
	private String userId;

	private AccountType type;

	private Long amount;

	@Transient
	private boolean insertable;

	public AccountEntity(boolean insertable) {
		this.insertable = insertable;
	}

	@Override
	public boolean isNew() {
		return insertable;
	}
}
