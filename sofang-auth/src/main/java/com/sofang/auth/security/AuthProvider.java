package com.sofang.auth.security;

import com.sofang.base.entity.User;
import com.sofang.base.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * 自定义安全认证
 *
 * @since 1.0
 *
 * @version 1.0
 *
 * @author gegf
 */
public class AuthProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    private final Md5PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String passwordInput = (String) authentication.getCredentials();

        User user = userService.findUserByName(username);

        if(user == null){
            throw new AuthenticationCredentialsNotFoundException("authError");
        }
        //user.getId() 加盐
        if(this.passwordEncoder.isPasswordValid(user.getPassword(), passwordInput, user.getId())){
            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        }
        throw new BadCredentialsException("authError");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
