package fr.ekinci.katabank.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;


@Table("transactions")
@Data
@NoArgsConstructor
public class TransactionEntity implements Persistable<String> {
	@Id
	private String id;

	@Column("user_id_from")
	private String userIdFrom;

	@Column("user_id_to")
	private String userIdTo;

	private LocalDateTime date;
	private Long amount;

	@Transient
	@JsonIgnore
	private boolean insertable;

	public TransactionEntity(boolean insertable) {
		this.insertable = insertable;
	}

	@Override
	@JsonIgnore
	public boolean isNew() {
		return insertable;
	}
}
