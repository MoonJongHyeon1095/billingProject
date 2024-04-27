package com.github.config.db;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DataSourceConfiguration {
    private static final String MAIN_DATASOURCE = "mainDataSource";
    private static final String REPLICA_DATASOURCE_1 = "replicaDataSource_1";
    private static final String REPLICA_DATASOURCE_2 = "replicaDataSource_2";

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @Value("${spring.datasource.username}")
    private String dataSourceUsername;

    @Value("${spring.datasource.password}")
    private String dataSourcePassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dataSourceDriverClassName;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(dataSourceUrl)
                .username(dataSourceUsername)
                .password(dataSourcePassword)
                .driverClassName(dataSourceDriverClassName)
                .build();
    }

//    @Bean(MAIN_DATASOURCE)
//    @ConfigurationProperties(prefix = "spring.datasource.main.hikari")
//    public DataSource mainDataSource1() {
//        log.info("------------mainDB_initialized------------");
//        return DataSourceBuilder.create()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean(REPLICA_DATASOURCE_1)
//    @ConfigurationProperties(prefix = "spring.datasource.replica1.hikari")
//    public DataSource replicaDataSource1() {
//        log.info("------------replicaDB1_initialized------------");
//        return DataSourceBuilder.create()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//    @Bean(REPLICA_DATASOURCE_2)
//    @ConfigurationProperties(prefix = "spring.datasource.replica2.hikari")
//    public DataSource replicaDataSource2() {
//        log.info("------------replicaDB2_initialized------------");
//        return DataSourceBuilder.create()
//                .type(HikariDataSource.class)
//                .build();
//    }
//
//
//    @Bean
//    @DependsOn({MAIN_DATASOURCE, REPLICA_DATASOURCE_1, REPLICA_DATASOURCE_2})
//    public DataSource routingDataSource(
//        @Qualifier(MAIN_DATASOURCE) DataSource mainDataSource,
//        @Qualifier(REPLICA_DATASOURCE_1) DataSource replicaDataSource_1,
//        @Qualifier(REPLICA_DATASOURCE_2) DataSource replicaDataSource_2
//    ){
//        final RoutingDataSource routingDataSource = new RoutingDataSource();
//        final Map<Object, Object> dataSource = new HashMap<>();
//        dataSource.put("main", mainDataSource);
//        dataSource.put("replica1", replicaDataSource_1);
//        dataSource.put("replica2", replicaDataSource_2);
//        routingDataSource.setTargetDataSources(dataSource);
//        routingDataSource.setDefaultTargetDataSource(mainDataSource);
//        return routingDataSource;
//    }
//
//    @Bean
//    @DependsOn("routingDataSource")
//    public LazyConnectionDataSourceProxy dataSourceProxy(DataSource dataSource){
//        return new LazyConnectionDataSourceProxy(dataSource);
//    }

}
