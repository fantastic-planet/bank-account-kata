package fr.ekinci.katabank.repository;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * https://github.com/r2dbc/r2dbc-h2
 * https://www.baeldung.com/spring-data-r2dbc
 */
@Profile("database_h2")
@Configuration
@EnableTransactionManagement
@EnableR2dbcRepositories
public class H2DatabaseConfiguration extends AbstractR2dbcConfiguration {
	private static final String H2_URL = "r2dbc:h2:mem:///test?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

	@Bean
	@Override
	public ConnectionFactory connectionFactory() {
		return ConnectionFactories.get(H2_URL);
	}

	@Bean
	ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
		return new R2dbcTransactionManager(connectionFactory);
	}
}
