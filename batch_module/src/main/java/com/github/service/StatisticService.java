package com.github.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private final Job dailyStatisticJob;
    private final JobLauncher jobLauncher;
    private final Job clearDataJob;

    public StatisticService(
            JobLauncher jobLauncher,
            @Qualifier("dailyStatisticJob") Job dailyStatisticJob,
            @Qualifier("clearDataJob") Job clearDataJob
    ) {
        this.dailyStatisticJob = dailyStatisticJob;
        this.jobLauncher = jobLauncher;
        this.clearDataJob = clearDataJob;
    }


    /**
     * 크론 표현식에서 시간 필드는 UTC(협정 세계시)를 기준으로 합니다.
     * 따라서 한국 서울 시간(KST)으로 설정하려면 UTC 시간에 9시간을 더하거나 타임존 명시
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 6 1 * * ?", zone = "Asia/Seoul")
    public void runDailyStatisticJob() throws Exception {
        try{
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // Job parameter에 고유한 값 추가
                    .toJobParameters();
            jobLauncher.run(dailyStatisticJob, jobParameters);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Scheduled(cron = "0 06 17 * * ?", zone = "Asia/Seoul")
    public void runClearDataJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // Job parameter에 고유한 값 추가
                    .toJobParameters();
            jobLauncher.run(clearDataJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
