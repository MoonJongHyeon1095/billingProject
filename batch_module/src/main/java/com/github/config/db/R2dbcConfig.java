package com.github.config.db;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import reactor.core.publisher.Mono;

import java.time.Duration;

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

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(Duration.ofSeconds(157)) // 유휴 연결 최대 유지 시간
                .maxLifeTime(Duration.ofSeconds(150))
                .maxSize(50) // 풀의 최대 크기
                .build();

        ConnectionPool pool = new ConnectionPool(configuration);
        testConnection(pool);
        return pool;
    }

    private void testConnection(ConnectionPool pool) {
        Mono.from(pool.create())
                .flatMap(connection -> {
                    log.info("------------R2DBC_initialized------------ {}:{}", host, 3306);
                    return Mono.from(connection.close());
                })
                .onErrorResume(e -> {
                    log.error("Failed to connect to R2DBC database: {}", e.getMessage(), e);
                    return Mono.empty();
                })
                .subscribe();
    }
}
