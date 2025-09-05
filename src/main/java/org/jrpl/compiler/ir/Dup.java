/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Duplicate the top element of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code DUP}.
 *
 * <pre>
 *   stack before: [a]
 *   stack after : [a, a]
 * </pre>
 */
public final class Dup implements Instruction {

    /** Singleton instance (no state). */
    public static final Dup INSTANCE = new Dup();
    private Dup() {
    }
}
