//package com.github.config.redis;
//
//import com.github.service.RedisService;
//import org.springframework.data.redis.connection.Message;
//import org.springframework.data.redis.connection.MessageListener;
//
//public class RedisExpirationListener implements MessageListener {
//    private final RedisService redisService;
//
//    public RedisExpirationListener(RedisService redisService) {
//        this.redisService = redisService;
//    }
//    @Override
//    public void onMessage(Message message, byte[] pattern) {
//        String expiredKey = new String(message.getBody());
//        // 만료된 키에 대한 처리 수행
//        redisService.updateIfTtlExpired(expiredKey);
//    }
//}
