package com.javamaster.fiveinarow.controllers.DTO;

import com.javamaster.fiveinarow.models.Player;

import lombok.Data;

@Data
public class ConnectRequest {
  private Player player;
  private String gameId;
}