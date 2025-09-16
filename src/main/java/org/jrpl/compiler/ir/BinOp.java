/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Binary arithmetic operation (addition, subtraction, multiplication, division, exponentiation).
 *
 * <p>Consumes two numbers from the stack and pushes the result.</p>
 *
 * <p>Example RPL:
 * <pre>{@code
 *  2 3 +
 * }</pre>
 */
public final class BinOp implements Instruction {

    /**
     * Supported kinds of binary operations.
     */
    public enum Kind {
        /** Addition: {@code a + b} */ ADD,
        /** Subtraction: {@code a - b} */ SUB,
        /** Multiplication: {@code a * b} */ MUL,
        /** Division: {@code a / b} */ DIV,
        /** Exponentiation: {@code a ^ b} */ POW
    }

    /**
     * The selected operation kind.
     */
    public final Kind kind;

    /**
     * Creates a new binary operation IR node.
     *
     * @param kind the operation kind
     */
    public BinOp(Kind kind) {
        this.kind = kind;
    }
}
