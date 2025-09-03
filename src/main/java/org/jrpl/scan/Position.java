/*
 * Copyright (c) 2025 Massimo Costantini
 * Licensed under the Apache License, Version 2.0
 * See the LICENSE file in the project root for full license information
 */

package org.jrpl.scan;

/**
 * Represents an absolute position in the source code.
 * A position is immutable and is used to annotate tokens
 * and diagnostics with their location.
 *
 * <h2>Example</h2>
 * <pre>{@code
 * Position position = new Position(5, 2, 3);
 * System.out.println(position); // "Position[index=5, line=2, column=3]"
 * }</pre>
 *
 * @param index zero-based offset in the source string
 * @param line one-based line number
 * @param column one-based column number
 */
public record Position(int index, int line, int column) {

    /** Initial position (offset 0, line 1, column 1) */
    public static final Position ZERO = new Position(0, 1, 1);
}
