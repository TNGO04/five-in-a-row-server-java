package com.javamaster.fiveinarow.model;

import lombok.Data;

@Data
public class Game {
  private String gameId;
  private GameStatus status;


  //private Player currentPlayer = null;
  private Player playerX, playerO;
  private IGameBoard board;
  private final int boardDimension;

  public static int WIN_CONDITION = 5;
  public static char X = 'X', O = 'O', EMPTY = ' ';

  public Game(int dimension) {
    boardDimension = dimension;
  }
}
