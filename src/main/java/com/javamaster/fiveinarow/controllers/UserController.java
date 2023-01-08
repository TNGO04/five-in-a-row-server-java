package com.javamaster.fiveinarow.controllers;

import com.javamaster.fiveinarow.models.User;
import com.javamaster.fiveinarow.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UserController {
  @Autowired
  private UserService service;

  @PostMapping("/register")
  public ResponseEntity register(@RequestBody User user) {
    User existingUser = service.getUserByUsername(user.getUsername());

    if (existingUser == null) {
      user = service.createUser(user);
      return ResponseEntity.ok(user);
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exists");
  }


  @PostMapping("/login")
  public ResponseEntity login(@RequestBody User user) {
    User existingUser = service.verifyUser(user);

    if (existingUser != null) {
      return ResponseEntity.ok(existingUser);
    }
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Credentials not found.");
  }
}