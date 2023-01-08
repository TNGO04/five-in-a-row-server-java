package com.javamaster.fiveinarow.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Document("games")
public class Game {
  @Id
  private String _id;
  private GameStatus status;
  private User playerX, playerO;
  private User winner;
  private GameBoard board;
  public static int WIN_CONDITION = 5;
}
