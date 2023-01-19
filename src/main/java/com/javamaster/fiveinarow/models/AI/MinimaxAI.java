package com.javamaster.fiveinarow.models.AI;

import com.javamaster.fiveinarow.models.Game;
import com.javamaster.fiveinarow.models.Symbol;
import com.javamaster.fiveinarow.models.board.GameBoard;
import com.javamaster.fiveinarow.models.streak.StreakList;

import java.util.ArrayList;
import java.util.List;


/**
 * Class for MinimaxAI object, using minimax algorithm to make game moves.
 */
public class MinimaxAI extends AbstractAI{
  public static final double unblockedFourUtility = 1.0, blockedFourUtility = 0.5;
  public static final double unblockedThreeUtility = 0.5, blockedThreeUtility = 0.1;
  public static final double unblockedTwoUtility = 0.04, blockedTwoUtility = 0.01;
  public static final double utilityCap = 0.9;
  public static final int boardDimension = 20;
  private Symbol aiPlayer, opponent;
  private int searchDepth;

  /**
   * Constructor.
   *
   * @param aiPlayer        player controlled by AI
   * @param opponent        opponent player of aiPlayer
   */
  public MinimaxAI(Symbol aiPlayer, Symbol opponent)
          throws IllegalArgumentException {
    super(boardDimension);
    if ((boardDimension <= 0) || (aiPlayer == null) || (opponent == null)) {
      throw new IllegalArgumentException("Input object is null.");
    }
    if (aiPlayer == opponent) {
      throw new IllegalArgumentException("AI Player and opponent must be different.");
    }
    this.aiPlayer = aiPlayer;
    this.opponent = opponent;
    this.searchDepth = 2;
  }

  /**
   * Constructor.
   *
   * @param aiPlayer     player controlled by AI
   * @param opponent     opponent player of aiPlayer
   * @param searchDepth  depth of search tree for minimax
   */
  public MinimaxAI(Symbol aiPlayer, Symbol opponent, int searchDepth) throws IllegalArgumentException {
    this(aiPlayer, opponent);
    this.searchDepth = searchDepth;
  }

  /**
   * Given a list of streaks, calculate the utility score.
   *
   * @param list StreakList object
   * @return utility of StreakList, ranging [0...1]
   */
  public double calculateUtility(StreakList list) {
    int maxStreak = list.getMaxStreakLength();

    if (maxStreak == Game.WIN_CONDITION) {
      return 1.0;
    } else if (maxStreak == 0) {
      return 0;
    } else {
      double utility = 0.0;
      for (int i = maxStreak - 2; i >= 0; i--) {
        int unblockedCount = list.getStreak(i).getUnblockedCount();
        int blockedCount = list.getStreak(i).getCount() - unblockedCount;
        switch (i + 2) {
          case 2:
            utility += (unblockedCount * unblockedTwoUtility + blockedCount * blockedTwoUtility);
            break;
          case 3:
            utility += (unblockedCount * unblockedThreeUtility
                    + blockedCount * blockedThreeUtility);
            break;
          case 4:
            utility += (unblockedCount * unblockedFourUtility + blockedCount * blockedFourUtility);
            // if a game-ending streak of length 4 is detected, return maximum utility 1.
            if (utility >= 1) {
              return 1.0;
            }
            break;
        }
      }
      /* since a utility of 1 is reserved for only when a game ending streak is detected (unblocked
      streak of 4 or multiple blocked streaks of four, utility for lists without game-ending streaks
      is capped at 0.9*/
      utility = Math.min(utility, utilityCap);
      return utility;
    }
  }

  /**
   * Calculate utility value of a board state, based on streaks found for aiPlayer and opponent.
   *
   * @param boardState boardState to calculate utility from
   * @return utility
   */
  public double calculateUtilityOfBoardState(GameBoard boardState) {
    StreakList aiStreak = boardState.checkBoardForStreaks(aiPlayer);
    StreakList opponentStreak = boardState.checkBoardForStreaks(opponent);

    double aiUtility = this.calculateUtility(aiStreak);
    double opponentUtility = this.calculateUtility(opponentStreak);

    // if either ai or opponent has a game-ending streak, prioritize that by only returning
    // 1 for ai or -1 for opponent.
    if ((aiUtility == 1) && (opponentUtility < 1)) {
      return 1;
    }
    if ((aiUtility < 1) && (opponentUtility == 1)) {
      return -1;
    }

    return (aiUtility - opponentUtility);
  }

  /**
   * Maximizer function.
   *
   * @param boardState    last board state
   * @param maxUtility    max utility previously found
   * @param lastMove      last move made
   * @param depth         depth of minimax tree
   * @return              return utility
   */
  public double maximizer(GameBoard boardState, double maxUtility, int[] lastMove, int depth) {
    if (boardState.checkWinningMove(lastMove[0], lastMove[1])) {
      if (boardState.returnPosition(lastMove[0], lastMove[1]).getCharacter() == aiPlayer.getCharacter()) {
        return 4.0;
      } else {
        return -4.0;
      }
    }
    if (boardState.isOutOfMoves() || (depth >= this.searchDepth)) {
      return this.calculateUtilityOfBoardState(boardState);
    }

    double utility = Double.NEGATIVE_INFINITY;
    List<int[]> actionSet = this.getActionSet(boardState);

    for (int[] newMove : actionSet) {
      utility = Math.max(utility, minimizer(boardState.getBoardState(newMove[0], newMove[1], aiPlayer),
              utility, newMove, depth + 1));
      if (utility > maxUtility) {
        break;
      }
    }
    return utility;
  }

  /**
   * Minimizer function.
   *
   * @param boardState    last board state
   * @param minUtility    minimum utility previously found
   * @param lastMove      last move made
   * @param depth         depth of minimax tree
   * @return              return utility
   */
  public double minimizer(GameBoard boardState, double minUtility, int[] lastMove, int depth) {
    if (boardState.checkWinningMove(lastMove[0], lastMove[1])) {
      if (boardState.returnPosition(lastMove[0], lastMove[1]).getCharacter() == aiPlayer.getCharacter()) {
        return 4;
      } else {
        return -4;
      }
    }
    if (boardState.isOutOfMoves() || (depth >= this.searchDepth)) {
      return this.calculateUtilityOfBoardState(boardState);
    }

    double utility = Double.POSITIVE_INFINITY;
    List<int[]> actionSet = this.getActionSet(boardState);

    for (int[] newMove : actionSet) {
      utility = Math.min(utility, maximizer(boardState.getBoardState(newMove[0], newMove[1], opponent),
              utility, newMove, depth + 1));
      if (utility < minUtility) {
        break;
      }
    }
    return utility;
  }

  /**
   * Minimax optimizer.
   *
   * @param boardState  current board state
   * @param lastMove    last move made on board
   * @param firstMove   if this is the first move
   * @return            optimal move
   */
  public int[] getOptimalMove(GameBoard boardState, int[] lastMove, boolean firstMove) {
    if (firstMove) {
      return this.getRandomMove();
    }

    List<int[]> optimalMoveList = new ArrayList<int[]>();
    List<int[]> actionSet = this.getActionSet(boardState);

    double utility = Double.NEGATIVE_INFINITY;
    double moveUtility;
    double maxStreak = Double.NEGATIVE_INFINITY;
    int currStreak;
    GameBoard newBoardState;

    for (int[] newMove : actionSet) {
      newBoardState = boardState.getBoardState(newMove[0], newMove[1], aiPlayer);
      moveUtility = minimizer(newBoardState, utility, newMove, 1);
      currStreak = newBoardState.checkMaximumConsecutive(newMove[0], newMove[1]);

      if ((moveUtility == utility)) {
        if (maxStreak == currStreak) {
          optimalMoveList.add(newMove);
        } else if (maxStreak < currStreak) {
          optimalMoveList.clear();
          optimalMoveList.add(newMove);
          maxStreak = currStreak;
        }
      }
      if (moveUtility > utility) {
        utility = moveUtility;
        optimalMoveList.clear();
        optimalMoveList.add(newMove);
        maxStreak = currStreak;
      }
    }
    System.out.println("Max Utility is: " + utility);
    return this.getRandomMove(optimalMoveList);
  }
}
