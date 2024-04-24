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
    private final Job weeklyStatisticJob;
    private final Job monthlyStatisticJob;


    public DailyStatisticService(JobLauncher jobLauncher,
                                 @Qualifier("dailyStatisticJob") Job dailyStatisticJob,
                                 @Qualifier("weeklyStatisticJob") Job weeklyStatisticJob,
                                 @Qualifier("monthlyStatisticJob") Job monthlyStatisticJob
    ) {
        this.jobLauncher = jobLauncher;
        this.dailyStatisticJob = dailyStatisticJob;
        this.weeklyStatisticJob = weeklyStatisticJob;
        this.monthlyStatisticJob = monthlyStatisticJob;
    }

    /**
     * 크론 표현식에서 시간 필드는 UTC(협정 세계시)를 기준으로 합니다.
     * 따라서 한국 서울 시간(KST)으로 설정하려면 UTC 시간에 9시간을 더하거나 타임존 명시
     * 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 30 22 * * ?", zone = "Asia/Seoul")
    public void runDailyStatisticsJob() {
        try {
            jobLauncher.run(dailyStatisticJob, new JobParameters());
        } catch (Exception e) {
            // Handle any exceptions that occur during job execution
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 35 22 * * ?", zone = "Asia/Seoul")
    public void runWeeklyStatisticJob() {
        try {
            jobLauncher.run(weeklyStatisticJob, new JobParameters());
        } catch (Exception e) {
            // Handle any exceptions that occur during job execution
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0 40 22 * * ?", zone = "Asia/Seoul")
    public void monthlyStatisticJob() {
        try {
            jobLauncher.run(monthlyStatisticJob, new JobParameters());
        } catch (Exception e) {
            // Handle any exceptions that occur during job execution
            e.printStackTrace();
        }
    }

}
