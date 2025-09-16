/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Duplicates the top element of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code DUP}.</p>
 *
 * <p>Stack effect:
 * <pre>
 *   before: [a]
 *   after : [a, a]
 * </pre>
 */
public final class Dup implements Instruction {

    /**
     * Singleton instance (stateless).
     */
    public static final Dup INSTANCE = new Dup();

    // Prevent external instantiation
    private Dup() {
    }
}
