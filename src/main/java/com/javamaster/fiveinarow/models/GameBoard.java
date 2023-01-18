package com.javamaster.fiveinarow.models;

import lombok.Data;

@Data
public class GameBoard{
  public static final int MINDIM = 5, MAXDIM = 99;

  private static final int boardDimension = 20; //number of columns/rows of board
  private Symbol[][] board;
  private int nMoves;
  public static final Symbol starting = Symbol.X;

  /**
   * Constructor for GameBoard.
   *
   * @param boardDimension boardDimension of board (height/width)
   * @throws IllegalArgumentException if boardDimension is not within range
   */
  public GameBoard() throws IllegalArgumentException {
    this.nMoves = 0;
    this.board = new Symbol[boardDimension][boardDimension];
    this.initializeBoard();
  }

  /**
   * Initialize board with empty character.
   */
  public void initializeBoard() {
    for (int row = 0; row < this.getBoardDimension(); row++) {
      for (int col = 0; col < this.getBoardDimension(); col++) {
        this.board[row][col] = Symbol.EMPTY;
      }
    }
  }

  private int getBoardDimension() {
    return boardDimension;
  }

  /**
   * Copy constructor.
   *
   * @param other GameBoard object to be copied
   */
  public GameBoard(GameBoard other) throws IllegalArgumentException {
    this();
    for (int i = 0; i < other.getBoardDimension(); i++) {
      this.board[i] = other.board[i].clone();
    }
    this.setNMoves(other.getNMoves());
  }

  /**
   * Check if the board is out of moves (filled with non-blank symbol).
   *
   * @return true if board is filled, false if not
   */
  public boolean isOutOfMoves() {
    if (nMoves < Math.pow(this.getBoardDimension(), 2)) {
      return false;
    }
    else return true;
  }

  /**
   * Check if position/move is within the bounds of board.
   *
   * @param row row of position
   * @param col col of position
   * @return true if on board, false if out of bounds
   */
  public boolean isOnBoard(int row, int col) {
    return ((row < this.boardDimension) && (col < this.boardDimension) && (row >= 0) && (col >= 0));
  }

  /**
   * Return the character at a certain position on board.
   *
   * @param col column of position
   * @param row row of position
   * @return character at position
   * @throws IllegalArgumentException if move is out of range on the board
   */
  public Symbol returnPosition(int row, int col) throws IllegalArgumentException {
    if (!isOnBoard(row, col)) {
      throw new IllegalArgumentException("Position is out of board range");
    }
    return this.board[row][col];
  }

  /**
   * Check if a position on board is empty.
   *
   * @param row row of position
   * @param col col of position
   * @return true if position is empty, false if not
   */
  public boolean isEmpty(int row, int col) {
    return (this.returnPosition(row, col) == Symbol.EMPTY);
  }

  /**
   * Check if a move is legal (position not already taken and within the boardDimension of board).
   *
   * @param row row of position to be checked
   * @param col col of position to be checked
   * @return true if legal, false if not
   */
  public boolean isLegalMove(int row, int col) {
    // check that move is within board's dimension and is not already taken
    return ((this.isOnBoard(row, col)) && (this.isEmpty(row, col)));
  }

  /**
   * Add a move to the board.
   *
   * @param row    row of position where the move will be added
   * @param col    col of position
   * @param symbol player symbol to be added at position
   * @return true if added successfully (legal move), false if not
   */
  public boolean addMove(int row, int col, Symbol symbol) {
    if (symbol == Symbol.EMPTY) {
      System.out.println("Player symbol cannot be blank.\n");
      return false;
    }
    // if move is legal, add symbol to board
    if (this.isLegalMove(row, col)) {
      this.board[row][col] = symbol;
      nMoves++;
      return true;
    } else {
      return false;
    }
  }

  /**
   * Check if a position is adjacent to any move currently on board.
   *
   * @param row row of position
   * @param col col of position
   * @return true if position is adjacent only to empty cells, false if not
   *          a move off-board is technically defined as disconnected because it is not adjacent to
   *          any other moves
   */
  public boolean isDisconnected(int row, int col) {
    if (this.isLegalMove(row, col)) {
      int above = Math.max(0, row - 1);
      int below = Math.min(this.boardDimension - 1, row + 1);
      int left = Math.max(0, col - 1);
      int right = Math.min(this.boardDimension - 1, col + 1);

      return ((this.isEmpty(above, left)) && (this.isEmpty(above, col))
              && (this.isEmpty(above, right)) && (this.isEmpty(row, left))
              && (this.isEmpty(row, right)) && (this.isEmpty(below, left))
              && (this.isEmpty(below, col)) && (this.isEmpty(below, right)));
    }
    return true;
  }

  /**
   * Return an array representing a row of board.
   *
   * @param row row index
   * @return char array representing row
   */
  public Symbol[] getRow(int row) throws IllegalArgumentException {
    if ((row < 0) || (row > this.boardDimension)) {
      throw new IllegalArgumentException("Row index is out of board.");
    }
    return this.board[row];
  }

  /**
   * Return an array representing a column of board.
   *
   * @param col row index
   * @return char array representing column
   */
  public Symbol[] getColumn(int col) throws IllegalArgumentException {
    if ((col < 0) || (col > this.boardDimension)) {
      throw new IllegalArgumentException("Column index is out of board.");
    }
    Symbol[] array = new Symbol[this.boardDimension];
    for (int row = 0; row < this.boardDimension; row++) {
      array[row] = this.returnPosition(row, col);
    }
    return array;
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
  public Symbol[] getDiagonal(int row1, int col1, int row2, int col2) {
    boolean isDiagonal = (((row1 + col1) == (row2 + col2)) || ((row1 - col1) == (row2 - col2)));

    if (isOnBoard(row1, col1) && isOnBoard(row2, col2)
            && isDiagonal) {
      int arrayLength = Math.abs(row1 - row2) + 1;
      Symbol[] array = new Symbol[arrayLength];
      int directionRow = Integer.compare(row1, row2);
      int directionCol = Integer.compare(col1, col2);

      for (int row = row1, col = col1, i = 0; i < arrayLength;
           row -= directionRow, col -= directionCol, i++) {
        array[i] = this.returnPosition(row, col);
      }
      return array;
    }
    return null;
  }

  /**
   * Represent board with string, highlighting last move.
   *
   * @param r   row of last move
   * @param c   column of last move
   * @return String object which visually represents the board.
   */
  public String toString(int r, int c) {
    // make top border
    String s = "____";
    for (int col = 0; col < this.getBoardDimension(); col++) {
      s += "___";
    }
    s += "\n";
    s += "|  |";
    // make column header
    for (int col = 0; col < this.getBoardDimension(); col++) {
      s += String.format("%02d", col) + "|";
    }
    s += "\n";
    for (int row = 0; row < this.getBoardDimension(); row++) {
      // row header
      s += String.format("|%02d|", row);
      //print out character on board, separated by '|'
      for (int col = 0; col < this.getBoardDimension(); col++) {
        if ((r == row) && (c == col)) {
          s += this.returnPosition(row, col).getCharacter() + "_|";
        }
        else {
          s += this.returnPosition(row, col).getCharacter() + " |";
        }
      }
      s += "\n";
    }
    // make bottom border
    s += "----";
    for (int col = 0; col < this.getBoardDimension(); col++) {
      s += "---";
    }
    s += "\n";
    return s;
  }

  /**
   * Find the length of the longest horizontal streak that move was a part of.
   *
   * @param r, c   represents a move made on the board
   * @param search board subset to search within
   * @return length of the longest horizontal streak that move was a part of
   */
  public int checkConsecutiveHorizontal(int r, int c, BoardSubset search) {
    Symbol symbol = this.returnPosition(r, c);
    int count = 1;
    // traverse horizontally from move to the right
    for (int col = c + 1; col <= search.getRightCol(); col++) {
      if (this.returnPosition(r, col) == symbol) {
        count++;
      } else {
        break;
      }
    }
    // traverse horizontally from move to the left
    for (int col = c - 1; col >= search.getLeftCol(); col--) {
      if (this.returnPosition(r, col) == symbol) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * Find the length of the longest vertical streak that move was a part of.
   *
   * @param r, c  represents a move made on the board
   * @param search board subset to search within
   * @return length of the longest vertical streak that move was a part of
   */
  public int checkConsecutiveVertical(int r, int c, BoardSubset search) {
    Symbol symbol = this.returnPosition(r, c);
    int count = 1;

    // traverse from move to bottom row
    for (int row = r + 1; row <= search.getBotRow(); row++) {
      if (this.returnPosition(row, c) == symbol) {
        count++;
      } else {
        break;
      }
    }
    // traverse from move to top row
    for (int row = r - 1; row >= search.getTopRow(); row--) {
      if (this.returnPosition(row, c) == symbol) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  /**
   * On the 2 diagonals the move was made, within board subset, find the length of the longest
   * diagonal streak that move was a part of.
   *
   * @param move   move made
   * @param search board subset around the move
   * @return length of the longest diagonal streak that move was a part of
   */
  public int checkConsecutiveDiag(int r, int c, BoardSubset search) {
    Symbol symbol = this.returnPosition(r, c);
    int count1 = 1;
    // traverse diagonally from move to top left
    for (int row = r - 1, col = c - 1;
         (row >= search.getTopRow()) && (col >= search.getLeftCol());
         row--, col--) {
      if (this.returnPosition(row, col) == symbol) {
        count1++;
      } else {
        break;
      }
    }
    //  traverse diagonally from move to bottom right
    for (int row = r + 1, col = c + 1;
         (row <= search.getBotRow()) && (col <= search.getRightCol());
         row++, col++) {
      if (this.returnPosition(row, col) == symbol) {
        count1++;
      } else {
        break;
      }
    }
    // traverse diagonally from move to top right
    int count2 = 1;
    // construct diagonal array northwest by traversing from move (inclusive) to top right
    for (int row = r - 1, col = c + 1;
         (row >= search.getTopRow()) && (col <= search.getRightCol());
         row--, col++) {
      if (this.returnPosition(row, col) == symbol) {
        count2++;
      } else {
        break;
      }
    }
    // traverse diagonally from move to bottom left
    for (int row = r + 1, col = c - 1;
         (row <= search.getBotRow()) && (col >= search.getLeftCol());
         row++, col--) {
      if (this.returnPosition(row, col) == symbol) {
        count2++;
      } else {
        break;
      }
    }
    //return max count of the two diagonals
    return Math.max(count1, count2);
  }


  /**
   * Check if the last move made result in a win condition for last Player.
   *
   * @param r, c last move made
   * @return true if win condition is met, false if not
   */
  public boolean checkWinningMove(int r, int c) {
    return (this.checkMaximumConsecutive(r, c) >= Game.WIN_CONDITION);
  }

  /**
   * Find the longest streak length the last move was a part of.
   *
   * @param r, c last move made on board
   * @return longest streak length containing last move
   */
  public int checkMaximumConsecutive(int r, int c) {
    BoardSubset range = new BoardSubset(new int[]{r, c}, this.getBoardDimension(), Game.WIN_CONDITION);
    return Math.max(Math.max(this.checkConsecutiveHorizontal(r, c, range),
                    this.checkConsecutiveVertical(r, c, range)),
            this.checkConsecutiveDiag(r, c, range));
  }

  /**
   * Return board object after a potential move is made.
   *
   * @param r, c move to be made
   * @return new board state
   */
  public GameBoard getBoardState(int r, int c, Symbol symbol) throws IllegalArgumentException {
    // if move is illegal with current board state, throw exception
    if (!this.isLegalMove(r, c)) {
      throw new IllegalArgumentException("Invalid move");
    }

    //get deep copy of board and add move
    GameBoard newBoard = new GameBoard(this);
    newBoard.addMove(r, c, symbol);
    return newBoard;
  }

  @Data
  /**
   * BoardSubset stores row and column indexes that describe a portion of the board.
   */
  private class BoardSubset {
    private final int topRow;
    private final int botRow;
    private final int leftCol;
    private final int rightCol;

    /**
     * Constructor.
     *
     * @param topRow   row coordinate at the top of subset
     * @param botRow   row coordinate at the bottom of subset
     * @param leftCol  column coordinate leftmost of subset
     * @param rightCol column coordinate rightmost of subset
     */
    public BoardSubset(int topRow, int botRow, int leftCol, int rightCol)
            throws IllegalArgumentException {
      if ((topRow < 0) || (botRow < 0) || (leftCol < 0) || (rightCol < 0)) {
        throw new IllegalArgumentException("BoardSubset parameter cannot be negative");
      }

      this.topRow = topRow;
      this.botRow = botRow;
      this.leftCol = leftCol;
      this.rightCol = rightCol;
    }

    /**
     * Constructor.
     * Given a center, define the board subset that includes all moves that are RADIUS moves away
     * from the center.
     *
     * @param center center of board subset
     * @param radius   radius of board subset.
     */
    public BoardSubset(int[] center, int boundDimension, int radius)
            throws IllegalArgumentException {
      if (radius < 0) {
        throw new IllegalArgumentException("Search radius cannot be negative.");
      }

      int centerRow = center[0];
      int centerCol = center[1];

      // use min and max to make sure board subset indexes are not beyond the dimension of the board
      this.topRow = Math.max(0, centerRow - (radius - 1));
      this.leftCol = Math.max(0, centerCol - (radius - 1));
      this.botRow = Math.min(boundDimension - 1, centerRow + (radius - 1));
      this.rightCol = Math.min(boundDimension - 1, centerCol + (radius - 1));
    }
  }
}
