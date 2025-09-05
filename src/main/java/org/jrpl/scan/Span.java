/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.scan;

/**
 * Represents a half-open interval of source text,
 * defined by a start and end position.
 *
 * <p>Spans are used to annotate tokens and diagnostics,
 * so that error messages can point to the relevant
 * region of code.
*
 * <h2>Example:</h2>
 * <pre>{@code
 * Position start = new Position(0, 1, 1);
 * Position end   = new Position(2, 1, 3);
 * Span span = new Span(start, end);
 * System.out.println(span); // prints "line 1:1 - line 1:3"
 * }</pre>
 *
 * @param start inclusive start position
 * @param end exclusive end position
 */
public record Span(Position start, Position end) {
    @Override public String toString() {
        return "line " + start.line() + ":" + start.column() + " - " + end.line() + ":" + end.column();
    }
}
