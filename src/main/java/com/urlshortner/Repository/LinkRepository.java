package com.urlshortner.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.urlshortner.Entity.Link;

public interface LinkRepository extends MongoRepository<Link, String> {

    List<Link> findByUserId(String userId);

    boolean existsByCustomCode(String customCode);

    List<Link> findByActiveFalseAndActivateAtBefore(LocalDateTime now);

    List<Link> findByActiveTrueAndExpiresAtBefore(LocalDateTime now);

    Optional<Link> findByCustomCode(String code);

    List<Link> findByExpiresAtBetween(LocalDateTime from, LocalDateTime to);

        

    
}
