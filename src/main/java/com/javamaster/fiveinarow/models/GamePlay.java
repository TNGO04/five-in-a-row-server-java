package com.javamaster.fiveinarow.models;

import lombok.Data;

@Data
public class GamePlay {
  private Symbol symbol;
  private int rowCoordinate;
  private int colCoordinate;
  private String gameId;
}
