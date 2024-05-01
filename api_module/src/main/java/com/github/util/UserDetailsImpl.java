package com.github.util;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
public class UserDetailsImpl implements UserDetails {
    final String email;
    final String password;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 여기서는 유저의 권한을 반환해야 합니다. 실제 어플리케이션에서는 해당 유저의 권한 목록을 로딩하여 반환합니다.
        return Collections.emptyList(); // 현재는 권한 없음으로 설정
    }
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email; // email을 username으로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 크레덴셜이 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정 활성화
    }
}
