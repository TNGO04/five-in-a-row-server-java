package com.javamaster.fiveinarow.models.user;

//singleton AI user
public class AIPlayer {
  private static User AIinstance = null;

  public static void createInstance() {
    AIinstance = new User();
    AIinstance.setUsername("Computer");
    AIinstance.setPassword("");
    AIinstance.set_id("");
  }

  public static User getInstance() {
    return AIinstance;
  }
}
