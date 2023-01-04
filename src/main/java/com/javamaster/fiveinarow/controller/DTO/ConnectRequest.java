package com.javamaster.fiveinarow.controller.DTO;

import com.javamaster.fiveinarow.model.Player;

import lombok.Data;

@Data
public class ConnectRequest {
  private Player player;
  private String gameId;
}