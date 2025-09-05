/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.scan;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SourceTest {

  @Test
  void tracksLinesAndColumns() {
    Source source = new Source("ab\nc");
    assertFalse(source.eof());
    assertEquals('a', source.cursor());
    assertEquals('a', source.next()); // (1,1) -> (1,2)
    assertEquals(2, source.position().column());
    assertEquals('b', source.next()); // (1,2) -> (1,3)
    assertEquals(3, source.position().column());
    assertEquals('\n', source.next()); // newline -> (2,1)
    assertEquals(2, source.position().line());
    assertEquals(1, source.position().column());
    assertEquals('c', source.next());
    assertTrue(source.eof());
  }

  @Test
  void matchWorks() {
    Source source = new Source(">>=");
    assertTrue(source.match('>'));
    assertTrue(source.match('>'));
    assertFalse(source.match('>'));
    assertTrue(source.match('='));
    assertTrue(source.eof());
  }

  @Test
  void sliceReturnsSubstring() {
    Source source = new Source("hello");
    Position start = source.position();
    source.next(); source.next(); source.next(); // hel
    assertEquals("hel", source.slice(start));
  }
}
