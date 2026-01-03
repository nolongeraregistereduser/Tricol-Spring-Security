package com.tricol.springboottricolapi.security;

import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAppRepository userRepository;
    private final PermissionService permissionService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<String> permissions = permissionService.getUserPermissions(user);
        return UserDetailsImpl.build(user, permissions);
    }
}
