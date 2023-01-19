package com.javamaster.fiveinarow.controllers;

import com.javamaster.fiveinarow.controllers.DTO.ConnectRequest;
import com.javamaster.fiveinarow.exceptions.GameNotFoundException;
import com.javamaster.fiveinarow.exceptions.InvalidGameException;
import com.javamaster.fiveinarow.exceptions.InvalidParameterException;
import com.javamaster.fiveinarow.models.Game;
import com.javamaster.fiveinarow.models.GamePlay;
import com.javamaster.fiveinarow.models.user.User;
import com.javamaster.fiveinarow.services.GameService;
import com.javamaster.fiveinarow.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = "*")
/**
 * GameController handles HTTP request
 */
public class GameController {

  private final GameService gameService;
  private final UserService userService;
  @Autowired
  private final SimpMessagingTemplate simpMessagingTemplate;

  /**
   * Handle game start through POST request.
   * @return  response entity with game information
   */
  @PostMapping("/game/start")
  public ResponseEntity<Game> start(@RequestBody User user) {
    log.info("Start game request by {}", user.getUsername());
    return ResponseEntity.ok(gameService.createGame(user));
  }

  /**
   * Handle second player connecting to existing game through POST request
   * @param request POST request containing player and gameId
   * @return  game
   * @throws InvalidGameException gameId does not match any in existing database
   * @throws InvalidParameterException if game is full
   */
  @PostMapping("/game/connect")
  public ResponseEntity connect(@RequestBody ConnectRequest request) throws InvalidGameException, GameNotFoundException {
    try {
      log.info("Connect request: {}", request);
      Game game = gameService.connectToGame(request.getUser(), request.getGameId());
      simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.get_id(), game);
      return ResponseEntity.ok(game);
    }
    catch (GameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Game with ID not found");
    }
    catch (InvalidGameException e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Game not eligible to connect");
    }
  }

  /**
   * Connect to random game.
   * @return  random game player is connected to
   * @throws GameNotFoundException if no available game
   */
  @PostMapping("/game/connect/random")
  public ResponseEntity connectRandom(@RequestBody User user) throws GameNotFoundException {
    try {
      log.info("Connect to random game by {}", user.getUsername());
      return ResponseEntity.ok(gameService.connectToRandomGame(user));
    }
    catch (GameNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No game available to connect");
    }
  }

  /**
   * Connect to a game with AI
   */
  @PostMapping("/game/connect/computer")
  public ResponseEntity connectToAIGame(@RequestBody User user) throws GameNotFoundException {
    try {
      log.info("Connect to game with AI by {}", user.getUsername());
      Game game = gameService.connectToAIGame(user);
      simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.get_id(), game);
      return ResponseEntity.ok(game);
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No game available to connect");
    }
  }

  /**
   * Receive GamePlay JSON object through POST request and execute gameplay.
   * @param request request containing GamePlay JSON object
   * @return game
   * @throws GameNotFoundException
   * @throws InvalidGameException
   */
  @PostMapping("/game/gameplay")
  public ResponseEntity gamePlay(@RequestBody GamePlay request) throws GameNotFoundException, InvalidGameException {
    try {
      log.info("Gameplay: {}", request);
      Game game = gameService.gamePlay(request);
      simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.get_id(), game);
      return ResponseEntity.ok(game);
    }
    catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }

  @GetMapping("/game/gameplay/computer")
  public ResponseEntity gamePlayComputer(@RequestParam("id") String gameId) throws GameNotFoundException {
    try {
      log.info("AI Gameplay requested for {}", gameId);
      Game game = gameService.makeAIMove(gameId);
      simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.get_id(), game);
      return ResponseEntity.ok(game);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
  }
}
