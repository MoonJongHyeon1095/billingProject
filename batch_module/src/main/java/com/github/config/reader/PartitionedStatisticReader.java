//package com.github.config.reader;
//
//import com.github.config.db.DataSourceConfiguration;
//import com.github.config.mapper.WatchHistoryRowMapper;
//import com.github.domain.WatchHistory;
//import org.springframework.batch.core.StepExecution;
//import org.springframework.batch.core.annotation.BeforeStep;
//import org.springframework.batch.item.ExecutionContext;
//import org.springframework.batch.item.database.JdbcCursorItemReader;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.sql.DataSource;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//
///**
// * JdbcCursorItemReader는 생성 시점에 DataSource가 필요
// * 그러나 PartitionedStatisticReader 클래스의 구현에서는 DataSource를 beforeStep 메소드에서 설정
// * 스프링이 PartitionedStatisticReader 빈을 초기화하는 과정에서 필요한 DataSource가 없어 예외가 발생
// *
// * 해결 방안: PartitionedStatisticReader의 생성자에서 DataSource를 받아
// * 바로 setDataSource 메소드를 사용해 설정
// */
//@Component
//public class PartitionedStatisticReader extends JdbcCursorItemReader<WatchHistory> {
//    private final int minValue;
//    private final int maxValue;
//
//
//    public PartitionedStatisticReader(
//            @Qualifier("dataSource") DataSource dataSource,
//            int minValue,
//            int maxValue
//    ) {
//        this.minValue = minValue;
//        this.maxValue = maxValue;
//        setDataSource(dataSource);
//        setSql("SELECT 1"); // 임시 쿼리로 초기화
//        setRowMapper(new WatchHistoryRowMapper());
//        // 이후 필요한 설정들을 추가합니다.
//    }
//
//    /**
//     * ExecutionContext에 접근해야 한다면, @BeforeStep을 사용하는 것이 맞습니다
//     */
//    @BeforeStep
//    @Transactional(readOnly = true)
//    public void beforeStep(StepExecution stepExecution) {
//        String tablePartitionKey = LocalDate.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
//
//        ExecutionContext executionContext = stepExecution.getExecutionContext();
//        int startVideoId = executionContext.getInt("startVideoId");
//        int endVideoId = executionContext.getInt("endVideoId");
//
//        setSql(String.format(
//                "SELECT videoId, playedTime, adViewCount " +
//                        "FROM WatchHistory " +
//                        "WHERE createdAt = '%s'" +
//                        " AND videoId BETWEEN %d AND %d",
//                tablePartitionKey, startVideoId, endVideoId));
//        setRowMapper(new WatchHistoryRowMapper());
//    }
//}
