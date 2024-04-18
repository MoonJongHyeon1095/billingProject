package com.github.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.github.domain.User;

import java.util.Optional;

@Mapper
public interface UserMapper {
    /**
     * 새 유저 생성
     * @param user The user to insert.
     * @return The number of rows affected.
     */
    int insertUser(User user);

    /**
     * 이메일로 유저 조회
     * @param email The email to search for.
     * @return The user if found, null otherwise.
     */
    Optional<User> findUserByEmail(@Param("email") String email);
}