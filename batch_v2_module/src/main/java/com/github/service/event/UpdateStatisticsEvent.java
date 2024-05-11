package com.github.service.event;

import org.springframework.context.ApplicationEvent;


public class UpdateStatisticsEvent extends ApplicationEvent {

    public UpdateStatisticsEvent(Object source) {
        super(source);
    }

}
