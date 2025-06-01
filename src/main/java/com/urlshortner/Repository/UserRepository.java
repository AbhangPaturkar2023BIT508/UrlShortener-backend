package com.urlshortner.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.urlshortner.Entity.User;

public interface UserRepository extends MongoRepository<User, String> {
    public Optional<User> findByEmail(String email);
}
