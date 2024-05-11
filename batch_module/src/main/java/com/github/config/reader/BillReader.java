package com.github.config.reader;

import com.github.config.mapper.VideoStatisticRowMapper;
import com.github.domain.VideoStatistic;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 동시성 관리
 0. 혹시 이 인터페이스는 페이징에 Offset을 쓰는 만행을 저지른 것 아닐까?
 - https://giron.tistory.com/161
 - 그렇지 않다고 합니다.

 1. 페이지 크기 설정: JdbcPagingItemReader의 페이지 크기(pageSize)는 각 청크(chunk)의 크기와 일치해야.
 - 이 설정은 각 트랜잭션에서 처리할 아이템의 수를 정의하며, 이를 통해 동시에 접근하는 데이터가 겹치지 않도록 한다.

 2. 유니크한 정렬 키: sortKey가 유니크해야 하며, 이를 통해 데이터가 정렬되고 페이징 처리.
 - 이 설정은 각 스레드가 서로 다른 데이터 세트를 읽도록 보장.

 3. 상태 관리: @StepScope는 각 스텝 실행마다 새로운 빈 인스턴스를 생성.
 - 각 스텝의 실행이 독립적이라는 것을 의미,
 - 여러 스텝 인스턴스가 동시에 실행되더라도 각 인스턴스가 서로 다른 데이터 세트를 처리하도록 할 수 있다.
 - @StepScope가 적용된 reader는 이 스텝이 실행될 때마다 새로운 인스턴스로 생성되므로, 각 스텝 실행은 고유한 리더 인스턴스를 사용
 - 데이터 격리: 각 스텝 인스턴스 데이터를 독립적으로 읽고, 다른 스텝의 실행과 데이터를 공유하지 않습니다.
 - 병렬 처리

 */
@Slf4j
@Configuration
public class BillReader {
    private final DataSource dataSource;

    public BillReader(@Qualifier("mysqlDataSource") DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<VideoStatistic> buildBillReader() {

        String today = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //String today = "2024-05-09";
        return new JdbcPagingItemReaderBuilder<VideoStatistic>()
                .name("reader")
                .pageSize(20)
                .fetchSize(20)
                .dataSource(dataSource)
                .rowMapper(new VideoStatisticRowMapper())
                .queryProvider(billQueryProvider())
                .parameterValues(Map.of(
                        "today", today, "range", 5000
                ))
                .build();
    }
    /**
     queryProvider.setDataSource
     - qlPagingQueryProviderFactoryBean에서 데이터베이스와 상호작용하는 쿼리 프로바이더를 생성하는데 사용
     - 쿼리 프로바이더는 페이징 쿼리를 생성하고, 데이터 소스에 연결하여 쿼리 실행을 담당
     - 데이터 소스를 설정함으로써 데이터베이스 유형과 설정에 맞는 적절한 페이징 쿼리를 생성

     reader.setDataSource(dataSource):
     - JdbcPagingItemReader에서 직접 데이터베이스 연결을 설정하는 데 사용
     - 이 설정은 실제로 데이터베이스에 접속하여 데이터를 읽어오는 역할

     */
    @Bean
    public PagingQueryProvider billQueryProvider() {

        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("SELECT videoId, dailyViewCount, dailyAdViewCount, createdAt");
        queryProvider.setFromClause("FROM VideoStatistic");
        queryProvider.setWhereClause("WHERE createdAt = :today AND videoId < :range");
        queryProvider.setSortKey("videoId");

        try {
            return queryProvider.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
