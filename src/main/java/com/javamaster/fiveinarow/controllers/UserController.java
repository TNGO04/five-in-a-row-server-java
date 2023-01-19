package com.javamaster.fiveinarow.controllers;

import com.javamaster.fiveinarow.controllers.DTO.CredentialRequest;
import com.javamaster.fiveinarow.models.user.User;
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
  public ResponseEntity register(@RequestBody CredentialRequest credentials) {
    User existingUser = service.getUserByUsername(credentials.getUsername());
    User newUser = new User();
    if (existingUser == null) {
      newUser.setPassword(credentials.getPassword());
      newUser.setUsername(credentials.getUsername());
      newUser = service.createUser(newUser);
      return ResponseEntity.ok(newUser);
    }
    else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Username already exists");
    }
  }


  @PostMapping("/login")
  public ResponseEntity login(@RequestBody CredentialRequest credentials) {
    User existingUser = service.verifyUser(credentials.getUsername(), credentials.getPassword());

    if (existingUser != null) {
      return ResponseEntity.ok(existingUser);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Credentials not found.");
  }
}
