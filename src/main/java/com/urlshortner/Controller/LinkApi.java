package com.urlshortner.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortner.Entity.Link;
import com.urlshortner.Services.LinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@CrossOrigin
@RequestMapping("/link")
public class LinkApi {
    @Autowired
    private LinkService linkService;

    @PostMapping("/create")
    public ResponseEntity<Link> createLink(@RequestBody Link link) {
        return new ResponseEntity<>(linkService.createLink(link), HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public List<Link> getAllLinks(@RequestParam String userId) {
        if (userId == null) {
            return new ArrayList<>(); // or throw a custom exception
        }
        return linkService.getAllLinks(userId);
    }

    @GetMapping("/checkCodeExists/{customCode}")
    public ResponseEntity<Boolean> checkCustomCodeExists(@PathVariable String customCode) {
        boolean exists = linkService.isCustomCodeExists(customCode);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable String id) {
        if (linkService.getLinkById(id).isPresent()) {
            linkService.deleteLink(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
