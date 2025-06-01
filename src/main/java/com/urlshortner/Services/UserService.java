package com.urlshortner.Services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.urlshortner.Dto.LoginDTO;
import com.urlshortner.Entity.User;
import com.urlshortner.Exception.UrlshortnerException;
import com.urlshortner.Repository.UserRepository;

@Service(value = "userService")
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User registerUser(User user) throws UrlshortnerException {
        Optional<User> optional = userRepository.findByEmail(user.getEmail());
        if (optional.isPresent())
            throw new UrlshortnerException("User has registered already.");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user);
        return userRepository.save(user);
    }

    public User loginUser(LoginDTO loginDto) throws UrlshortnerException {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UrlshortnerException("User is not registered."));
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword()))
            throw new UrlshortnerException("Invalid Credentials.");
        return user;

    }
}
