package com.urlshortner.Entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "links")
public class Link {

    @Id
    private String id;

    private String userId;

    private String originalUrl;

    private String customCode;

    private LocalDateTime createdAt;

    private LocalDateTime activateAt;

    private LocalDateTime expiresAt;

    private boolean notifyOn;

    private int clicks;

    private boolean active;
}
