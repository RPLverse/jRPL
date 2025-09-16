/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Pushes a constant {@code double} onto the runtime stack.
 *
 * <p>Example RPL:
 * <pre>{@code
 *  66
 * }</pre>
 */
public final class PushConst implements Instruction {

    /**
     * Constant value to be pushed.
     */
    public final double value;

    /**
     * Creates a new constant-push IR node.
     *
     * @param value constant numeric value to push
     */
    public PushConst(double value) {
        this.value = value;
    }
}
