package com.javamaster.fiveinarow.models;

import org.springframework.context.annotation.Bean;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Symbol {
  X('X'), O('O'), EMPTY(' ');

  private char character;
}
