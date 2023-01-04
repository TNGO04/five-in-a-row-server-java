package com.javamaster.fiveinarow.service;

import com.javamaster.fiveinarow.exception.GameNotFoundException;
import com.javamaster.fiveinarow.exception.InvalidGameException;
import com.javamaster.fiveinarow.exception.InvalidParameterException;
import com.javamaster.fiveinarow.model.Game;
import com.javamaster.fiveinarow.model.GameBoard;
import com.javamaster.fiveinarow.model.IGameBoard;
import com.javamaster.fiveinarow.model.GamePlay;
import com.javamaster.fiveinarow.model.Player;
import com.javamaster.fiveinarow.storage.GameStorage;

import org.springframework.stereotype.Service;

import java.util.UUID;

import lombok.AllArgsConstructor;

import static com.javamaster.fiveinarow.model.GameStatus.IN_PROGRESS;
import static com.javamaster.fiveinarow.model.GameStatus.NEW;

@Service
@AllArgsConstructor
public class GameService {
  private final static int DIMENSION = 20;

  public Game createGame(Player player) {
    Game game = new Game(DIMENSION);

    game.setBoard(new GameBoard());
    game.setGameId(UUID.randomUUID().toString());
    game.setPlayerX(player);
    game.setStatus(NEW);
    GameStorage.getInstance().setGame(game);
    return game;

  }

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

  public Game connectToRandomGame(Player playerO) throws GameNotFoundException {
    Game game = GameStorage.getInstance().getGames().values().stream().
                    filter(gameElement -> gameElement.getStatus().equals(NEW)).findFirst().
            orElseThrow(() -> new GameNotFoundException("Game not found."));

    game.setPlayerO(playerO);
    game.setStatus(IN_PROGRESS);
    GameStorage.getInstance().setGame(game);
    return game;
  }

  public Game gamePlay(GamePlay gamePlay) throws GameNotFoundException, InvalidGameException {
    String gameId = gamePlay.getGameId();
    if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
      throw new GameNotFoundException("Game not found");
    }

    Game game = GameStorage.getInstance().getGames().get(gameId);
    if (!game.getStatus().equals(IN_PROGRESS)) {
      throw new InvalidGameException("Cannot add move to game not in progress.");
    }

    IGameBoard board = game.getBoard();
    board.addMove(gamePlay.getRowCoordinate(),gamePlay.getColCoordinate(),gamePlay.getMoveSymbol());
    boolean isWon = board.checkWinningMove(gamePlay.getRowCoordinate(), gamePlay.getColCoordinate());

    GameStorage.getInstance().setGame(game);
    return game;
  }
}
