package com.github.config.db;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Slf4j
@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    @Value("${spring.r2dbc.host}")
    private String host;

    @Value("${spring.r2dbc.username}")
    private String username;

    @Value("${spring.r2dbc.password}")
    private String password;

    @Value("${spring.r2dbc.database}")
    private String database;

    @Bean(name = "r2dbcConnectionFactory")
    @Override
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(PROTOCOL, "r2dbc")
                .option(HOST, host)
                .option(PORT, 3306)
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(Option.valueOf("serverTimezone"), "Asia/Seoul")
                .build());

        testConnection(connectionFactory); // Test connection when creating the factory
        return connectionFactory;
    }

    private void testConnection(ConnectionFactory connectionFactory) {
        Mono.from(connectionFactory.create())
                .flatMap(connection -> {
                    log.info("Successfully connected to R2DBC database at {}:{}", host, 3306);
                    return Mono.from(connection.close());
                })
                .onErrorResume(e -> {
                    log.error("Failed to connect to R2DBC database: {}", e.getMessage(), e);
                    return Mono.empty();
                })
                .subscribe();
    }
}
