package com.sunrisedentalclinic.security;

import com.sunrisedentalclinic.model.StaffUser;
import com.sunrisedentalclinic.repository.StaffUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffUserDetailsService implements UserDetailsService {

    private final StaffUserRepository staffUserRepository;

    public StaffUserDetailsService(StaffUserRepository staffUserRepository) {
        this.staffUserRepository = staffUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser staffUser = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Staff user not found: " + username));

        return User.withUsername(staffUser.getUsername())
                .password(staffUser.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + staffUser.getRole())))
                .disabled(!staffUser.isEnabled())
                .build();
    }
}
