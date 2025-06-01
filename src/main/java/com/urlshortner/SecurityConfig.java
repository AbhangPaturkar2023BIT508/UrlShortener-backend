package com.urlshortner;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Disable CSRF for APIs (enable in production with care)
                .authorizeHttpRequests()
                .requestMatchers("/**").permitAll() // Allow public access to your API
                .anyRequest().authenticated()
                .and()
                .httpBasic(); // or .formLogin() or JWT based, depending on your app

        return http.build();
    }
}
