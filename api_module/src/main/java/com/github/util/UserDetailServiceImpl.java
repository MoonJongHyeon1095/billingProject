package com.github.util;

import com.github.domain.User;
import com.github.exception.UserErrorCode;
import com.github.exception.UserException;
import com.github.mapper.UserMapper;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * map 함수는 스트림의 각 요소를 변환하는 연산입니다.
 * 각 입력 요소에 대해 정의된 함수를 적용하고, 그 결과에 따라 새로운 값으로 구성된 새 스트림을 생성합니다.
 * map은 입력 스트림의 각 요소에 대해 동기적 연산을 하기 때문에 결과를 즉시 반환합니다.
 *
 * flatMap 연산도 스트림의 각 요소를 변환합니다만, map과 달리 각 요소를 변환한 결과가 스트림일 수 있으며,
 * 이렇게 생성된 여러 스트림을 평탄화하여 단일 스트림으로 만듭니다.
 * flatMap은 비동기 처리나 복잡한 스트림 변환 연산에 주로 사용됩니다.
 */
@Service
public class UserDetailServiceImpl implements ReactiveUserDetailsService {
    private final UserMapper userMapper;

    public UserDetailServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    @Override
    public Mono<UserDetails> findByUsername(String email) {
        return Mono.fromCallable(() -> userMapper.findUserByEmail(email))
                .subscribeOn(Schedulers.boundedElastic()) // 블로킹 호출을 처리하기 위해 별도의 스레드 풀을 사용합니다.
                .flatMap(userOptional -> userOptional
                        .map(user -> UserDetailsImpl.builder()
                                .password(user.getPassword())
                                .email(user.getEmail())
                                .build())
                        .map(Mono::just) // UserDetailsImpl을 Mono<UserDetails>로 변환
                        .orElseGet(() -> Mono.error(new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND))))
                .cast(UserDetails.class); // UserDetails 타입으로 캐스팅합니다.
    }
}
