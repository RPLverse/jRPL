/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Binary arithmetic operation.
 *
 * <p>Consumes two numbers from the stack and pushes the result.
 *
 * <h2>Supported operations:</h2>
 * <ul>
 *   <li>{@link Kind#ADD} – addition</li>
 *   <li>{@link Kind#SUB} – subtraction</li>
 *   <li>{@link Kind#MUL} – multiplication</li>
 *   <li>{@link Kind#DIV} – division</li>
 *   <li>{@link Kind#POW} – exponentiation</li>
 * </ul>
 *
 * <p>Example RPL: {@code +}</p>
 */
public final class BinOp implements Instruction {

    /**
     *  Operation kind.
     */
    public enum Kind {
       /** Addition: a + b. */ ADD,
       /** Subtraction: a - b. */ SUB,
       /** Multiplication: a * b. */ MUL,
       /** Division: a / b. */ DIV,
       /** Exponentiation: a ^ b. */ POW
    }

    /**
     * Selected operation kind.
     */
    public final Kind kind;

    /**
     * Creates a binary operation IR node.
     * @param kind the operation kind
     */
    public BinOp(Kind kind) {
        this.kind = kind;
    }
}
