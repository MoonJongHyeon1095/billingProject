package com.github.config.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfiguration {

    private final String dataSourceUrl;
    private final String dataSourceUsername;
    private final String dataSourcePassword;
    private final String dataSourceDriverClassName;

    @Autowired
    public DataSourceConfiguration(
            @Value("${spring.datasource.url}") String dataSourceUrl,
            @Value("${spring.datasource.username}") String dataSourceUsername,
            @Value("${spring.datasource.password}") String dataSourcePassword,
            @Value("${spring.datasource.driver-class-name}") String dataSourceDriverClassName
    ) {
        this.dataSourceUrl = dataSourceUrl;
        this.dataSourceUsername = dataSourceUsername;
        this.dataSourcePassword = dataSourcePassword;
        this.dataSourceDriverClassName = dataSourceDriverClassName;
    }

    @Primary
    @Bean
    public HikariDataSource mainDataSource() {
        log.info("------------mainDB_initialized------------");
        HikariDataSource dataSource = DataSourceBuilder.create()
                .url(dataSourceUrl)
                .username(dataSourceUsername)
                .password(dataSourcePassword)
                .driverClassName(dataSourceDriverClassName)
                .type(HikariDataSource.class)
                .build();
        return dataSource;
    }

    @Bean
    public HikariDataSource replicaDataSource1() {
        log.info("------------replicaDB1_initialized------------");
        HikariDataSource dataSource = DataSourceBuilder.create()
                .url(dataSourceUrl)
                .username(dataSourceUsername)
                .password(dataSourcePassword)
                .driverClassName(dataSourceDriverClassName)
                .type(HikariDataSource.class)
                .build();
        return dataSource;
    }

    @Bean
    public HikariDataSource replicaDataSource2() {
        log.info("------------replicaDB2_initialized------------");
        HikariDataSource dataSource = DataSourceBuilder.create()
                .url(dataSourceUrl)
                .username(dataSourceUsername)
                .password(dataSourcePassword)
                .driverClassName(dataSourceDriverClassName)
                .type(HikariDataSource.class)
                .build();
        return dataSource;
    }


    @Bean(name = "dataSource") // 'dataSource'로 명시적으로 지정
    public DataSource routingDataSource(
    ){
        final DataSource mainDataSource = mainDataSource();
        final DataSource replicaDataSource1 = replicaDataSource1();
        final DataSource replicaDataSource2 = replicaDataSource2();
        final RoutingDataSource routingDataSource = new RoutingDataSource();
        final Map<Object, Object> dataSource = new HashMap<>();
        dataSource.put("main", mainDataSource);
        dataSource.put("replica1", replicaDataSource1);
        dataSource.put("replica2", replicaDataSource2);
        routingDataSource.setTargetDataSources(dataSource);
        routingDataSource.setDefaultTargetDataSource(mainDataSource);
        return routingDataSource;
    }

//    @Bean
//    @DependsOn("dataSource")
//    public LazyConnectionDataSourceProxy dataSourceProxy(DataSource dataSource){
//        return new LazyConnectionDataSourceProxy(routingDataSource());
//    }

    @Bean(name = "transactionManager") //transactionManager라고 명시하지 않으면 찾지 못한다.
    public JdbcTransactionManager batchTransactionManager() {
        return new JdbcTransactionManager(routingDataSource());
    }

}
