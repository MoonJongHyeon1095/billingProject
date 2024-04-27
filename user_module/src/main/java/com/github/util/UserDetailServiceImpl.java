package com.github.util;

import com.github.domain.User;
import com.github.exception.UserErrorCode;
import com.github.exception.UserException;
import com.github.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserMapper userMapper;

    public UserDetailServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(final String email){
         User user = userMapper.findUserByEmail(email).orElseThrow(()->new UserException.UserNotFoundException(UserErrorCode.USER_NOT_FOUND));
         return UserDetailsImpl.builder()
                 .password(user.getPassword()).email(user.getEmail()).build();
    }
}
