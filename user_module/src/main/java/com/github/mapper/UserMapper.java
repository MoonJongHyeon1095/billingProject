package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.domain.User;

import java.util.Optional;

@Mapper
public interface UserMapper {
    /**
     * 새 유저 생성
     *
     * @param user The user to insert.
     */
    void insertUser(User user);

    /**
     * 이메일로 유저 조회
     * @param email The email to search for.
     * @return The user if found, null otherwise.
     */
    Optional<User> findUserByEmail(@Param("email") String email);

    /**
     * 리프레시 토큰 갱신
     * @param user domain/User객체
     */
    void updateRefreshToken(User user);
}