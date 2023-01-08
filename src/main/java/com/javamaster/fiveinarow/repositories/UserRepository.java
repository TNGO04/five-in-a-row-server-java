package com.javamaster.fiveinarow.repositories;

import com.javamaster.fiveinarow.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
  @Query("{'username': ?0}")
  User findByUsername(String username);

  @Query("{'username':  ?0, 'password':  ?1}")
  User findByCredentials(String username, String password);
}
