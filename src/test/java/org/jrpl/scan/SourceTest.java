/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.scan;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SourceTest {

  @Test
  @DisplayName("Tracks line and column positions while scanning")
  void tracksLinesAndColumns() {

    // Initialize source with two lines: "ab" and "c"
    Source source = new Source("ab\nc");

    // At the beginning, it should not be at EOF
    assertFalse(source.eof());

    // Cursor starts at 'a'
    assertEquals('a', source.cursor());

    // Read 'a': moves from (line 1, col 1) to (line 1, col 2)
    assertEquals('a', source.next());
    assertEquals(2, source.position().column());

    // Read 'b': now at (line 1, col 3)
    assertEquals('b', source.next());
    assertEquals(3, source.position().column());

    // Read newline: resets column, increments line
    assertEquals('\n', source.next());
    assertEquals(2, source.position().line());
    assertEquals(1, source.position().column());

    // Read 'c', then reach EOF
    assertEquals('c', source.next());
    assertTrue(source.eof());
  }

  @Test
  @DisplayName("Lexer.match() recognizes expected tokens")
  void matchWorks() {

    // Initialize source with ">>="
    Source source = new Source(">>=");

    // Match consumes the expected character if present
    assertTrue(source.match('>'));
    assertTrue(source.match('>'));
    assertFalse(source.match('>'));
    assertTrue(source.match('='));

    // After consuming all, EOF
    assertTrue(source.eof());
  }

  @Test
  @DisplayName("Source.slice() returns the expected substring")
  void sliceReturnsSubstring() {

    // Initialize source with "hello"
    Source source = new Source("hello");

    // Remember the starting position
    Position start = source.position();

    // Consume three characters ("hel")
    source.next();
    source.next();
    source.next();

    // Slice returns the substring from start to current position
    assertEquals("hel", source.slice(start));
  }
}
