package com.javamaster.fiveinarow.model;

import lombok.Data;

@Data
public class Game {
  private String gameId;
  private GameStatus status;
  private Player playerX, playerO;
  private GameBoard board;
  private final int boardDimension;
  public static int WIN_CONDITION = 5;

  public Game(int dimension) {
    boardDimension = dimension;
  }
}
