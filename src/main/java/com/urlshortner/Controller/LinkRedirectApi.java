package com.urlshortner.Controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortner.Services.LinkService;

@RestController
@CrossOrigin
@RequestMapping("/r")
public class LinkRedirectApi {
    @Autowired
    private LinkService linkService;

    // private static final String FRONTEND_BASE_URL = "http://localhost:5173";

    private static final String FRONTEND_BASE_URL = "https://shortlink-xdxw.onrender.com";

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirectToOriginal(@PathVariable String code) {
        URI redirectUri = linkService.getRedirectUri(code, FRONTEND_BASE_URL);
        // System.out.println(redirectUri);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(redirectUri)
                .build();
    }
}
