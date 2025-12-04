package com.coursehub.identity_service.security;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = userRepository.findByEmail(username).orElse(null);
            if (user == null) {
                user = userRepository.findByPhoneNumber(username).orElseThrow(() ->
                        new NotFoundException("User not found"));
            }
        }

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getUserRole(),
                user.getPassword()
        );


    }
}
