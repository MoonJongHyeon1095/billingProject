package com.github.config.listener.job_listener;

import com.github.service.event.UpdateStatisticsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;


@Slf4j
public class DailyUpdateJobListener implements JobExecutionListener {
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public DailyUpdateJobListener(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        eventPublisher.publishEvent(new UpdateStatisticsEvent(this));
        log.info("UpdateStatisticsEvent published");
    }
}
