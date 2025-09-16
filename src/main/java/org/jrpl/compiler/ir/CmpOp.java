/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Numeric comparison producing {@code 1.0} for true or {@code 0.0} for false.
 *
 * <p>Consumes two numbers from the stack and pushes the boolean-as-double result.</p>
 *
 * <p>Example RPL:
 * <pre>{@code
 *  5 3 >
 * }</pre>
 */
public final class CmpOp implements Instruction {

    /**
     * Supported comparison kinds.
     */
    public enum Kind {
        /** {@code a} {@literal >}  {@code b} */ GT,
        /** {@code a} {@literal <}  {@code b} */ LT,
        /** {@code a} {@literal >=} {@code b} */ GE,
        /** {@code a} {@literal <=} {@code b} */ LE,
        /** {@code a == b} */ EQ,
        /** {@code a != b} */ NE
    }

    /**
     * The selected comparison kind.
     */
    public final Kind kind;

    /**
     * Creates a new comparison operation IR node.
     *
     * @param kind the comparison kind
     */
    public CmpOp(Kind kind) {
        this.kind = kind;
    }
}
