package com.cloudify.demologin.security;

import com.cloudify.demologin.entity.Customer;
import com.cloudify.demologin.entity.User;
import com.cloudify.demologin.repository.CustomerRepository;
import com.cloudify.demologin.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public UserDetailsServiceImpl(UserRepository userRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return UserDetailsImpl.create(
                    user.get().getId(),
                    user.get().getUsername(),
                    user.get().getPassword(),
                    "ADMIN"
            );
        }

        Optional<Customer> customer = customerRepository.findByUsername(username);
        if (customer.isPresent()) {
            return UserDetailsImpl.create(
                    customer.get().getId(),
                    customer.get().getUsername(),
                    customer.get().getPassword(),
                    "CUSTOMER"
            );
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
