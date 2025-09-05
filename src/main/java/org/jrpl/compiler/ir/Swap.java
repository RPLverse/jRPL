/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Swap the top two elements of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code SWAP}.
 *
 * <pre>
 *   stack before: [a, b]
 *   stack after : [b, a]
 * </pre>
 */
public final class Swap implements Instruction {

    /** Singleton instance (no state). */
    public static final Swap INSTANCE = new Swap();
    private Swap() {
    }
}
