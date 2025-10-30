package com.coursehub.identity_service.security;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.IUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceConcrete implements UserDetailsService {
    private final IUserRepository userRepository;

    public UserDetailsServiceConcrete(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            user = userRepository.findByEmail(username).orElse(null);
            if (user == null) {
                user = userRepository.findByPhoneNumber(username).orElseThrow(() ->
                        new UserNotFoundException("User not found"));
            }
        }


        return new UserPrincipal(user);
    }
}
