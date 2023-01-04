package com.javamaster.fiveinarow.model;

public interface IGameBoard {
  int getBoardDimension();

  /**
   * Initialize board with empty character.
   */
  void initializeBoard();

  /**
   * Check if the board is out of moves (filled with non-blank symbol).
   *
   * @return true if board is filled, false if not
   */
  boolean isOutOfMoves();

  /**
   * Return the character at a certain position on board.
   *
   * @param col column of position
   * @param row row of position
   * @return character at position
   * @throws IllegalArgumentException if move is out of range on the board
   */
  char returnPosition(int row, int col) throws IllegalArgumentException;

  /**
   * Check if a move is legal (position not already taken and within the boardDimension of board).
   *
   * @param row row of position to be checked
   * @param col col of position to be checked
   * @return true if legal, false if not
   */
  boolean isLegalMove(int row, int col);

  /**
   * Add a move to the board.
   *
   * @param row    row of position where the move will be added
   * @param col    col of position
   * @param symbol player symbol to be added at position
   * @return true if added successfully (legal move), false if not
   */
  boolean addMove(int row, int col, char symbol);

  /**
   * Check if a position is adjacent to any move currently on board.
   *
   * @param row row of position
   * @param col col of position
   * @return true if position is adjacent only to empty cells, false if not
   *          a move off-board is technically defined as disconnected because it is not adjacent to
   *          any other moves
   */
  boolean isDisconnected(int row, int col);

  /**
   * Return an array representing a row of board.
   *
   * @param row row index
   * @return char array representing row
   */
  char[] getRow(int row) throws IllegalArgumentException;

  /**
   * Return an array representing a column of board.
   *
   * @param col row index
   * @return char array representing column
   */
  char[] getColumn(int col) throws IllegalArgumentException;

  /**
   * Get an array representing the diagonal between two position 1 and 2.
   *
   * @param row1 row of position 1
   * @param col1 col of position 1
   * @param row2 row of position 2
   * @param col2 col of position 2
   * @return array representing diagonal between 2 points, null if points are not diagonal
   */
  char[] getDiagonal(int row1, int col1, int row2, int col2);

  /**
   * Represent board with string, highlighting last move.
   *
   * @param r   row of last move
   * @param c   column of last move
   * @return String object which visually represents the board.
   */
  String toString(int r, int c);

  /**
   * Check if the last move made result in a win condition for last Player.
   *
   * @return true if win condition is met, false if not
   */
  boolean checkWinningMove(int row, int col);

  /**
   * Return board object after a potential move is made.
   *
   * @return new board state
   */
  IGameBoard getBoardState(int r, int c, Player player) throws IllegalArgumentException;
}
