package com.javamaster.fiveinarow.model;

import lombok.Data;

@Data
public class GamePlay {
  private char moveSymbol;
  private int rowCoordinate;
  private int colCoordinate;
  private String gameId;
}
