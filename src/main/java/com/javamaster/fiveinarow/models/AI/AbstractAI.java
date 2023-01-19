package com.javamaster.fiveinarow.models.AI;

import com.javamaster.fiveinarow.models.board.GameBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AbstractAI {
  protected int boardDimension;

  public AbstractAI(int boardDimension) throws IllegalArgumentException{
    if (boardDimension <= 0) {
      throw new IllegalArgumentException("Board dimension has to be positive.");
    }
    this.boardDimension = boardDimension;
  }

  /**
   * Get a random move with a bias for center of board.
   * A position at the center of board allows for more opportunities to expand; therefore, this
   * random move generator favors position mid-board.
   */
  public int[] getRandomMove() {
    Random rand = new Random();
    int numAverage = 8;

    int row = 0, column = 0;

    for (int i = 0; i < numAverage; i++) {
      row += rand.nextInt(boardDimension);
      column += rand.nextInt(boardDimension);
    }

    row = (int) Math.ceil(row / numAverage);
    column = (int) Math.ceil(column / numAverage);

    return new int[]{row, column};
  }

  /**
   * Get a random move from list of moves.
   */
  public int[] getRandomMove(List<int[]> actionList) {
    Random rand = new Random();
    int index = rand.nextInt(actionList.size());
    return actionList.get(index);
  }

  public List<int[]> getActionSet(GameBoard board) {
    List<int[]> actionSet = new ArrayList<int[]>();

    for (int row = 0; row < board.getBoardDimension(); row++) {
      for (int col = 0; col < board.getBoardDimension(); col++) {
        if (board.isEmpty(row, col) && !board.isDisconnected(row, col)) {
          actionSet.add(new int[]{row, col});
        }
      }
    }
    return actionSet;
  }

}
