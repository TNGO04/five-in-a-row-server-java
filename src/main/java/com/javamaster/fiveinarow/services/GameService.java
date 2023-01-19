package com.javamaster.fiveinarow.services;

import com.javamaster.fiveinarow.exceptions.GameNotFoundException;
import com.javamaster.fiveinarow.exceptions.InvalidGameException;
import com.javamaster.fiveinarow.exceptions.InvalidMoveException;
import com.javamaster.fiveinarow.exceptions.InvalidParameterException;
import com.javamaster.fiveinarow.models.AI.MinimaxAI;
import com.javamaster.fiveinarow.models.Game;
import com.javamaster.fiveinarow.models.board.GameBoard;
import com.javamaster.fiveinarow.models.GamePlay;
import com.javamaster.fiveinarow.models.GameStatus;
import com.javamaster.fiveinarow.models.Symbol;
import com.javamaster.fiveinarow.models.user.AIPlayer;
import com.javamaster.fiveinarow.models.user.User;
import com.javamaster.fiveinarow.repositories.GameRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import lombok.AllArgsConstructor;

import static com.javamaster.fiveinarow.models.GameStatus.IN_PROGRESS;
import static com.javamaster.fiveinarow.models.GameStatus.NEW;

/**
 * GameService class.
 */
@Service
@AllArgsConstructor
public class GameService {
  @Autowired
  private GameRepository gameRepository;
  private final static int DIMENSION = 20;
  private final static Symbol AIplayer = Symbol.X;
  private final static Symbol humanPlayer = Symbol.O;
  private final static MinimaxAI ai = new MinimaxAI(AIplayer, humanPlayer);
    /**
   * Create a PvP game with first player.
   * @param player  first player (X)
   * @return  game
   */
  public Game createGame(User player) {
    Game game = new Game();

    game.setBoard(new GameBoard());
    game.set_id(UUID.randomUUID().toString());
    game.setPlayerX(player);
    game.setStatus(NEW);
    return gameRepository.save(game);
  }

  /**
   * Connect 2nd player to a game in PvP mode
   * @param player2 player2
   * @param gameID gameID of game that player2 is connecting to
   * @return game
   * @throws InvalidParameterException no game with ID
   * @throws InvalidGameException game with ID is full
   */
  public Game connectToGame(User player2, String gameID) throws GameNotFoundException, InvalidGameException {
    Game game = gameRepository.findById(gameID).orElse(null);

    if (game == null) {
      throw new GameNotFoundException("No game exists with this ID");
    }

    if (game.getPlayerO() != null) {
      throw new InvalidGameException("Game is full.");
    }

    game.setPlayerO(player2);
    game.setStatus(IN_PROGRESS);
    return gameRepository.save(game);
  }

  /**
   * Connect player 2 to a random game.
   * @param player2 player2
   * @return  game
   * @throws GameNotFoundException  no game found
   */
  public Game connectToRandomGame(User player2) throws GameNotFoundException {
    // find list of new game and pick one randomly
    List<Game> randomList = gameRepository.findAll();

    Game game;
    if (randomList.size() == 0) {
      throw new GameNotFoundException("No new game found.");
    }
    else {
      Random rand = new Random();
      game = randomList.get(rand.nextInt(randomList.size()));
    }

    //change game status
    game.setPlayerO(player2);
    game.setStatus(IN_PROGRESS);
    return gameRepository.save(game);
  }


  /**
   *
   * @param user
   * @return
   */
  public Game connectToAIGame(User user) throws InvalidGameException, GameNotFoundException {
    if (AIPlayer.getInstance() == null) {
      AIPlayer.createInstance();
    }
    User AIuser = AIPlayer.getInstance();
    Game game = createGame(AIuser);
    game.setPlayerO(user);
    game.setStatus(IN_PROGRESS);
    return gameRepository.save(game);
  }

  /**
   * Execute a gameplay.
   * @param gamePlay  game play
   * @return  updated game
   * @throws GameNotFoundException game not found
   * @throws InvalidGameException game is either not started or already finished
   */
  public Game gamePlay(GamePlay gamePlay) throws GameNotFoundException, InvalidGameException, InvalidMoveException {
    Game game = gameRepository.findById(gamePlay.getGameId()).orElse(null);

    if (game == null) {
      throw new GameNotFoundException("No game exists with this ID");
    }

    if (!game.getStatus().equals(IN_PROGRESS)) {
      throw new InvalidGameException("Game has not been started yet.");
    }

    GameBoard board = game.getBoard();
    if (((board.getNMoves() % 2 == 0) && (gamePlay.getSymbol() != GameBoard.starting))
    || ((board.getNMoves() % 2 == 1) && (gamePlay.getSymbol() == GameBoard.starting))) {
      throw new InvalidMoveException("Currently not your turn.");
    }

    if (!board.addMove(gamePlay.getRowCoordinate(),gamePlay.getColCoordinate(),gamePlay.getSymbol())) {
      throw new InvalidMoveException("Move is not legal");
    }
    game = checkWinningMove(game, gamePlay);
    return gameRepository.save(game);
  }

  public Game checkWinningMove(Game game, GamePlay gamePlay) {
    boolean isWon = game.getBoard().checkWinningMove(gamePlay.getRowCoordinate(), gamePlay.getColCoordinate());
    if (isWon) {
      if (game.getWinner() != null) {
        return game;
      }
      if (gamePlay.getSymbol() == Symbol.X) {
        game.setWinner(game.getPlayerX());
      }
      else {
        game.setWinner(game.getPlayerO());
      };
      game.setStatus(GameStatus.FINISHED);
    }
    else if (game.getBoard().isOutOfMoves()) {
      game.setWinner(null);
      game.setStatus(GameStatus.FINISHED);
    }
    return game;
  }

  public Game makeAIMove(String gameId) throws GameNotFoundException {
    Game game = gameRepository.findById(gameId).orElse(null);

    if (game == null) {
      throw new GameNotFoundException("No game exists with this ID");
    }

    int[] currMove = new int[0];
    if (game.getBoard().getNMoves() == 0) {
      currMove = ai.getOptimalMove(game.getBoard(), null, true);
    } else {
      currMove = ai.getOptimalMove(game.getBoard(), currMove, false);
    }

    game.getBoard().addMove(currMove[0], currMove[1], AIplayer);

    GamePlay optimalPlay = new GamePlay();
    optimalPlay.setRowCoordinate(currMove[0]);
    optimalPlay.setColCoordinate(currMove[1]);
    optimalPlay.setSymbol(AIplayer);

    game = checkWinningMove(game, optimalPlay);
    return gameRepository.save(game);
  }

}
