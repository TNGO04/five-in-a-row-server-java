package com.javamaster.fiveinarow.service;

import com.javamaster.fiveinarow.exception.GameNotFoundException;
import com.javamaster.fiveinarow.exception.InvalidGameException;
import com.javamaster.fiveinarow.exception.InvalidParameterException;
import com.javamaster.fiveinarow.model.Game;
import com.javamaster.fiveinarow.model.GameBoard;
import com.javamaster.fiveinarow.model.GamePlay;
import com.javamaster.fiveinarow.model.Player;
import com.javamaster.fiveinarow.model.Symbol;
import com.javamaster.fiveinarow.storage.GameStorage;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

import static com.javamaster.fiveinarow.model.GameStatus.IN_PROGRESS;
import static com.javamaster.fiveinarow.model.GameStatus.NEW;

/**
 * GameService class.
 */
@Service
@AllArgsConstructor
public class GameService {
  private final static int DIMENSION = 20;
    /**
   * Create a PvP game with first player.
   * @param player  first player (X)
   * @return  game
   */
  public Game createGame(Player player) {
    Game game = new Game(DIMENSION);

    game.setBoard(new GameBoard(DIMENSION));
    game.setGameId(UUID.randomUUID().toString());
    game.setPlayerX(player);
    game.setStatus(NEW);
    GameStorage.getInstance().setGame(game);
    return game;
  }

  /**
   * Connect 2nd player to a game in PvP mode
   * @param player2 player2
   * @param gameID gameID of game that player2 is connecting to
   * @return game
   * @throws InvalidParameterException no game with ID
   * @throws InvalidGameException game with ID is full
   */
  public Game connectToGame(Player player2, String gameID) throws InvalidParameterException, InvalidGameException {
    if (!GameStorage.getInstance().getGames().containsKey(gameID)) {
      throw new InvalidParameterException("No game exists with this ID");
    }
    Game game = GameStorage.getInstance().getGames().get(gameID);

    if (game.getPlayerO() != null) {
      throw new InvalidGameException("Game is full.");
    }

    game.setPlayerO(player2);
    game.setStatus(IN_PROGRESS);
    GameStorage.getInstance().setGame(game);
    return game;
  }

  /**
   * Connect player 2 to a random game.
   * @param player2 player2
   * @return  game
   * @throws GameNotFoundException  no game found
   */
  public Game connectToRandomGame(Player player2) throws GameNotFoundException {
    // find list of new game and pick one randomly
    List<Game> randomList = GameStorage.getInstance().getGames().values().stream().
                    filter(game -> game.getStatus().equals(NEW))
                    .collect(Collectors.toList());

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
    GameStorage.getInstance().setGame(game);
    return game;
  }

  /**
   * Execute a gameplay.
   * @param gamePlay  game play
   * @return  updated game
   * @throws GameNotFoundException game not found
   * @throws InvalidGameException game is either not started or already finished
   */
  public Game gamePlay(GamePlay gamePlay) throws GameNotFoundException, InvalidGameException {
    String gameId = gamePlay.getGameId();

    if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
      throw new GameNotFoundException("Game not found");
    }

    Game game = GameStorage.getInstance().getGames().get(gameId);
    if (!game.getStatus().equals(IN_PROGRESS)) {
      throw new InvalidGameException("Cannot add move to game not in progress.");
    }


    GameBoard board = game.getBoard();


    board.addMove(gamePlay.getRowCoordinate(),gamePlay.getColCoordinate(),gamePlay.getSymbol());


    boolean isWon = board.checkWinningMove(gamePlay.getRowCoordinate(), gamePlay.getColCoordinate());

    if (isWon) {
      if (gamePlay.getSymbol() == Symbol.X) {
        game.setWinner(game.getPlayerX());
      }
      else {
        game.setWinner(game.getPlayerO());
      };
    }
    else if (board.isOutOfMoves()) {
      game.setWinner(null);
      //TO DO: end game without winner
    }

    GameStorage.getInstance().setGame(game);
    return game;
  }
}
