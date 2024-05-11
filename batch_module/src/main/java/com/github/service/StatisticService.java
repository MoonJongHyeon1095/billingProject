package com.github.service;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private final Job dailyStaticJobV1;
    private final JobLauncher jobLauncher;

    public StatisticService(
            JobLauncher jobLauncher,
            @Qualifier("dailyStatisticJobV1") Job dailyStaticJobV1
    ) {
        this.dailyStaticJobV1 = dailyStaticJobV1;
        this.jobLauncher = jobLauncher;
    }


    /**
     * 크론 표현식에서 시간 필드는 UTC(협정 세계시)를 기준으로 합니다.
     * 따라서 한국 서울 시간(KST)으로 설정하려면 UTC 시간에 9시간을 더하거나 타임존 명시
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 28 21 * * ?", zone = "Asia/Seoul")
    public void runDailyStatisticJob1() throws Exception {
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // Job parameter에 고유한 값 추가
                    .toJobParameters();
            jobLauncher.run(dailyStaticJobV1, jobParameters);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

//    @PostConstruct
//    public void runJobAtStartupForDebug() {
//        // 애플리케이션 시작 시 Job 실행
//        try {
//            runJob(dailyStaticJobV1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void runJob(Job job) throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("timestamp", System.currentTimeMillis()) // Job parameter에 고유한 값 추가
//                .toJobParameters();
//        jobLauncher.run(job, jobParameters);
//    }

}
