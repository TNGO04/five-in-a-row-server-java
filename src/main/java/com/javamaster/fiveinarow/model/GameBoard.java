package com.javamaster.fiveinarow.model;

public class GameBoard implements IGameBoard{
  @Override
  public int getBoardDimension() {
    return 0;
  }

  /**
   * Initialize board with empty character.
   */
  @Override
  public void initializeBoard() {

  }

  /**
   * Check if the board is out of moves (filled with non-blank symbol).
   *
   * @return true if board is filled, false if not
   */
  @Override
  public boolean isOutOfMoves() {
    return false;
  }

  /**
   * Return the character at a certain position on board.
   *
   * @param row row of position
   * @param col column of position
   * @return character at position
   * @throws IllegalArgumentException if move is out of range on the board
   */
  @Override
  public char returnPosition(int row, int col) throws IllegalArgumentException {
    return 0;
  }

  /**
   * Check if a move is legal (position not already taken and within the boardDimension of board).
   *
   * @param row row of position to be checked
   * @param col col of position to be checked
   * @return true if legal, false if not
   */
  @Override
  public boolean isLegalMove(int row, int col) {
    return false;
  }

  /**
   * Add a move to the board.
   *
   * @param row    row of position where the move will be added
   * @param col    col of position
   * @param symbol player symbol to be added at position
   * @return true if added successfully (legal move), false if not
   */
  @Override
  public boolean addMove(int row, int col, char symbol) {
    return false;
  }

  /**
   * Check if a position is adjacent to any move currently on board.
   *
   * @param row row of position
   * @param col col of position
   * @return true if position is adjacent only to empty cells, false if not
   * a move off-board is technically defined as disconnected because it is not adjacent to
   * any other moves
   */
  @Override
  public boolean isDisconnected(int row, int col) {
    return false;
  }

  /**
   * Return an array representing a row of board.
   *
   * @param row row index
   * @return char array representing row
   */
  @Override
  public char[] getRow(int row) throws IllegalArgumentException {
    return new char[0];
  }

  /**
   * Return an array representing a column of board.
   *
   * @param col row index
   * @return char array representing column
   */
  @Override
  public char[] getColumn(int col) throws IllegalArgumentException {
    return new char[0];
  }

  /**
   * Get an array representing the diagonal between two position 1 and 2.
   *
   * @param row1 row of position 1
   * @param col1 col of position 1
   * @param row2 row of position 2
   * @param col2 col of position 2
   * @return array representing diagonal between 2 points, null if points are not diagonal
   */
  @Override
  public char[] getDiagonal(int row1, int col1, int row2, int col2) {
    return new char[0];
  }

  /**
   * Represent board with string, highlighting last move.
   *
   * @param r row of last move
   * @param c column of last move
   * @return String object which visually represents the board.
   */
  @Override
  public String toString(int r, int c) {
    return null;
  }

  /**
   * Check if the last move made result in a win condition for last Player.
   *
   * @param row
   * @param col
   * @return true if win condition is met, false if not
   */
  @Override
  public boolean checkWinningMove(int row, int col) {
    return false;
  }

  /**
   * Return board object after a potential move is made.
   *
   * @param r
   * @param c
   * @param player
   * @return new board state
   */
  @Override
  public IGameBoard getBoardState(int r, int c, Player player) throws IllegalArgumentException {
    return null;
  }
}
