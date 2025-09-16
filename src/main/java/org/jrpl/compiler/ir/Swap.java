/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Swaps the top two elements of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code SWAP}.</p>
 *
 * <p>Stack effect:
 * <pre>
 *   before: [a, b]
 *   after : [b, a]
 * </pre>
 */
public final class Swap implements Instruction {

    /**
     * Singleton instance (stateless).
     */
    public static final Swap INSTANCE = new Swap();

    // Prevent external instantiation
    private Swap() {
    }
}
