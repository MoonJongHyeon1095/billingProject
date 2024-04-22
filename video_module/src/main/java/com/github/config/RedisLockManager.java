package com.github.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
public class RedisLockManager {
    private static final String REDIS_LOCK_TIMEOUT = "5000"; //5초 동안은 락 유지

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisScript<Boolean> lockScript;
    private final RedisScript<Boolean> unlockScript;


    /**
     1. lockScript 관련
     - redis.call: Redis에 명령을 호출하는 Lua 스크립트 함수
     - 'set': Redis에서 키-값 쌍을 설정하는 명령
     - KEYS[1]: 락을 나타내는 키 값. 스크립트 실행 시 이 값이 전달.
     - ARGV[1]: 설정할 값. 일반적으로 현재 타임스탬프나 고유한 식별자가 전달.
     - 'NX': 'Not eXists'의 약어로, 키가 이미 존재하지 않을 때만 설정하도록 지정. 락을 이미 획득한 스레드가 있는 경우, 이 스크립트 함수가 실패하도록.
     - 'PX': 밀리초 단위로 TTL(시간 제한). ARGV[2] 값만큼의 시간이 지나면 키가 자동으로 삭제. 잠금이 일정 시간 후에 해제되도록 보장.
     - ARGV[2]: TTL 값. 밀리초 단위, 이 기간이 지나면 락이 자동으로 해제.

     - 2. unLockScript 관련
     - redis.call('del', KEYS[1]): KEYS[1]은 삭제할 키
     - == 1: 삭제 명령의 반환 값이 1인지 확인. Redis에서 DEL 명령은 삭제된 키의 개수를 반환. 1이면 키가 성공적으로 삭제된 것.
     - 키가 성공적으로 삭제된 경우 true를 반환.

     * @param stringRedisTemplate
     */
    public RedisLockManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;

        this.lockScript = new DefaultRedisScript<>(
                "if redis.call('set', KEYS[1], ARGV[1], 'NX', 'PX', ARGV[2]) then return true else return false end",
                Boolean.class
        );

        this.unlockScript = new DefaultRedisScript<>(
                "if redis.call('del', KEYS[1]) == 1 then return true else return false end",
                Boolean.class
        );
    }

    /**
     * lock 취득에 성공하면 true 반환
     * @param lockKey
     * @param lockValue
     * @return
     */
    public boolean acquireLock(final String lockKey, final String lockValue) {
        Boolean lockAcquired = stringRedisTemplate.execute(
                lockScript,
                Collections.singletonList(lockKey),
                lockValue,
                REDIS_LOCK_TIMEOUT
        );

        return lockAcquired != null && lockAcquired;
    }

    public void releaseLock(final String lockKey, final String lockValue) {
        stringRedisTemplate.execute(
                unlockScript,
                Collections.singletonList(lockKey),
                lockValue
        );
    }
}
