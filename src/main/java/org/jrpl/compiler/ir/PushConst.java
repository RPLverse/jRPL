/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Pushes a constant double value onto the runtime stack.
 *
 * <p>Example RPL:
 * <pre>
 *   42    ; PushConst(42.0)
 * </pre>
 */
public final class PushConst implements Instruction {

    /** The constant value to be pushed. */
    public final double value;

    /**
     * Creates a constant-push IR node.
     *
     * @param value constant numeric value to push
     */
    public PushConst(double value) {
        this.value = value;
    }
}
