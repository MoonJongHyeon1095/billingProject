package com.github.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DailyStatisticService {
    private final JobLauncher jobLauncher;
    private final Job dailyStatisticJob;

    public DailyStatisticService(JobLauncher jobLauncher,
                                 @Qualifier("dailyStatisticJob") Job dailyStatisticJob) {
        this.jobLauncher = jobLauncher;
        this.dailyStatisticJob = dailyStatisticJob;
    }

    /**
     * 크론 표현식에서 시간 필드는 UTC(협정 세계시)를 기준으로 합니다.
     * 따라서 한국 서울 시간(KST)으로 설정하려면 UTC 시간에 9시간을 더하거나 타임존 명시
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 00 21 * * ?", zone = "Asia/Seoul")
    public void runDailyStatisticsJob() {
        try {
            jobLauncher.run(dailyStatisticJob, new JobParameters());
        } catch (Exception e) {
            // Handle any exceptions that occur during job execution
            e.printStackTrace();
        }
    }
}
