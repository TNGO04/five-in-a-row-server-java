package com.javamaster.fiveinarow.services;

import com.javamaster.fiveinarow.models.User;
import com.javamaster.fiveinarow.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepo;

  public User getUserByUsername(String username) {
    return userRepo.findByUsername(username);
  }

  public User verifyUser(User user) {
    User queried = userRepo.findByCredentials(user.getUsername(), user.getPassword());

    // omit password
    if (queried != null) {
      queried.setPassword("");
    }
    return queried;
  }

  public User createUser(User user) {
    userRepo.save(user);
    return user;
  }
}
