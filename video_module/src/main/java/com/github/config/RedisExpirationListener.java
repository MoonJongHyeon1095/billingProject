package com.github.config;

import com.github.service.ViewService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisExpirationListener implements MessageListener {
    private final ViewService viewService;

    public RedisExpirationListener(ViewService viewService) {
        this.viewService = viewService;
    }
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        // 만료된 키에 대한 처리 수행
        viewService.updateIfTtlExpired(expiredKey);
    }
}
