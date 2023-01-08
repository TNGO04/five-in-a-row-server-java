package com.javamaster.fiveinarow.controllers;

import com.javamaster.fiveinarow.controllers.DTO.ConnectRequest;
import com.javamaster.fiveinarow.exceptions.GameNotFoundException;
import com.javamaster.fiveinarow.exceptions.InvalidGameException;
import com.javamaster.fiveinarow.exceptions.InvalidParameterException;
import com.javamaster.fiveinarow.models.Game;
import com.javamaster.fiveinarow.models.GamePlay;
import com.javamaster.fiveinarow.models.Player;
import com.javamaster.fiveinarow.models.User;
import com.javamaster.fiveinarow.services.GameService;
import com.javamaster.fiveinarow.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")

/**
 * GameController handles HTTP request
 */
public class GameController {

  private final GameService gameService;
  private final UserService userService;
  private final SimpMessagingTemplate simpMessagingTemplate;

  /**
   * Handle game start through POST request.
   * @param player  player, parsed from body of request
   * @return  response entity with game information
   */
  @PostMapping("/start")
  public ResponseEntity<Game> start(@RequestBody Player player) {
    log.info("Start game request by {}", player);
    userService.createUser(new User());
    return ResponseEntity.ok(gameService.createGame(player));
  }

  /**
   * Handle second player connecting to existing game through POST request
   * @param request POST request containing player and gameId
   * @return  game
   * @throws InvalidGameException gameId does not match any in existing database
   * @throws InvalidParameterException if game is full
   */
  @PostMapping("/connect")
  public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidGameException, InvalidParameterException {
    log.info("Connect request: {}", request);
    return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
  }

  /**
   * Connect to random game.
   * @param player  player current trying to connect
   * @return  random game player is connected to
   * @throws GameNotFoundException if no available game
   */
  @PostMapping("/connect/random")
  public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws GameNotFoundException {
    log.info("Connect to random game by {}", player);
    return ResponseEntity.ok(gameService.connectToRandomGame(player));
  }

  /**
   * Receive GamePlay JSON object through POST request and execute gameplay.
   * @param request request containing GamePlay JSON object
   * @return game
   * @throws GameNotFoundException
   * @throws InvalidGameException
   */
  @PostMapping("/gameplay")
  public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws GameNotFoundException, InvalidGameException {
    log.info("Gameplay: {}", request);
    Game game = gameService.gamePlay(request);
    simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.get_id(), game);
    return ResponseEntity.ok(game);
  }
}
