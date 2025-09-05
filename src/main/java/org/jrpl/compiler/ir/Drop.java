/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Remove the top element of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code DROP}.
 *
 * <pre>
 *   stack before: [a]
 *   stack after : []
 * </pre>
 */
public final class Drop implements Instruction {

    /** Singleton instance (no state). */
    public static final Drop INSTANCE = new Drop();
    private Drop() {
    }
}
