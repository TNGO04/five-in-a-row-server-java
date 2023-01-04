package com.javamaster.fiveinarow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Symbol {
  X('X'), O('O'), EMPTY(' ');

  private char character;
}
