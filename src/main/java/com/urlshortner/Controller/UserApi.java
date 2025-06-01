package com.urlshortner.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortner.Dto.LoginDTO;
import com.urlshortner.Entity.User;
import com.urlshortner.Exception.UrlshortnerException;
import com.urlshortner.Services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@RequestMapping("/users")
public class UserApi {
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String hello() {
        return "Hello, world!";
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) throws UrlshortnerException {
        return new ResponseEntity<>(userService.registerUser(user), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<User> loginUser(@RequestBody LoginDTO loginDto) throws UrlshortnerException {
        return new ResponseEntity<>(userService.loginUser(loginDto), HttpStatus.OK);
    }

}
