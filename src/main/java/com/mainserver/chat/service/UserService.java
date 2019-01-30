package com.mainserver.chat.service;

import com.mainserver.chat.domain.RegisterRequest;
import com.mainserver.chat.entity.UserEntity;
import com.mainserver.chat.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void registerUser(RegisterRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(registerRequest.getEmail());
        userEntity.setFirstName(registerRequest.getFirstName());
        userEntity.setLastName(registerRequest.getLastName());
        userEntity.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userRepository.save(userEntity);

        SecurityContextHolder.getContext()
                .setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                registerRequest.getEmail(),
                                registerRequest.getPassword(),
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")))
                );
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(userEntity -> new User(
                        userEntity.getEmail(),
                        userEntity.getPassword(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
                ))
        .orElseThrow(() -> new UsernameNotFoundException("Username not found"));
    }
}
