package com.javamaster.fiveinarow.controllers.DTO;

import com.javamaster.fiveinarow.models.User;

import lombok.Data;

@Data
public class ConnectRequest {
  private User user;
  private String gameId;
}