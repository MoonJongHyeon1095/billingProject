package com.github.mapper;

import com.github.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface UserMapper {
    /**
     * 이메일로 유저 조회
     * @param email The email to search for.
     * @return The user if found, null otherwise.
     */
    @Select(
            "SELECT * FROM User WHERE email = #{email}")
    Optional<User> findUserByEmail(@Param("email") String email);

}