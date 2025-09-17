/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.scan;

/**
 * Character stream with line and column tracking.
 *
 * <p>The lexer uses {@code Source} to read characters one at a time
 * while keeping track of the current position.
 * It also supports simple matching and slicing substrings.
 *
 * <p>Example:
 * <pre>{@code
 * Source source = new Source("12 + 34");
 *
 * Position start = source.position();
 * while (!source.eof() && Character.isDigit(source.cursor())) {
 *   source.next();
 * }
 * Span span = new Span(start, source.position());
 *
 * System.out.println(source.slice(start));
 * System.out.println(span);
 * }</pre>
 *
 * Produces:
 * <pre>{@code
 * 12
 * line 1:1 - line 1:3
 * }</pre>
 */
public final class Source {

  // Underlying source text and current cursor (offset, line, column)
  private final String source;
  private int i = 0, line = 1, column = 1;

  /**
   * Creates a new character stream from the given source string.
   *
   * @param source the source string (non-null, uses empty string if {@code null})
   */
  public Source(String source) {
    this.source = source == null ? "" : source;
  }

  /**
   * Checks whether the end of input has been reached.
   *
   * @return {@code true} if all characters have been consumed
   */
  public boolean eof() {
      return i >= source.length();
  }

  /**
   * Returns the current character without consuming it.
   *
   * @return the current character
   * @throws IndexOutOfBoundsException if already at EOF
   */
  public char cursor() {
    if (eof()) throw new IndexOutOfBoundsException("EOF");
    return source.charAt(i);
  }

  /**
   * Consumes and returns the current character.
   * Updates line and column counters accordingly.
   *
   * @return the character just consumed
   * @throws IndexOutOfBoundsException if already at EOF
   */
  public char next() {
    char c = cursor();
    i++;
    if (c == '\n') {
        line++; column = 1;
    } else {
        column++;
      }
    return c;
  }

  /**
   * If the next character matches the expected one, consume it and return {@code true}.
   * Otherwise, return {@code false} and do not consume anything.
   *
   * @param expected the expected character
   * @return {@code true} if matched and consumed; {@code false} otherwise
   */
  public boolean match(char expected) {
    if (eof() || source.charAt(i) != expected) return false;
    next();
    return true;
  }

  /**
   * Returns the current position in the source.
   *
   * @return the current position (offset, line, column)
   */
  public Position position() {
      return new Position(i, line, column);
  }

  /**
   * Extracts the substring from the given start position up to (but excluding)
   * the current position.
   *
   * @param start the start position
   * @return the substring between {@code start.index()} (inclusive) and the current offset (exclusive)
   */
  public String slice(Position start) {
      return source.substring(start.index(), i);
  }
}
