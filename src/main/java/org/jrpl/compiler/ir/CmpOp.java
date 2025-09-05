/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Comparison operation between two numeric values.
 *
 * <p>Consumes two numbers from the stack and pushes {@code 1.0} if the
 * comparison is true or {@code 0.0} otherwise.
 *
 * <h2>Supported operations:</h2>
 * <ul>
 *   <li>{@link Kind#GT} – greater than</li>
 *   <li>{@link Kind#LT} – less than</li>
 *   <li>{@link Kind#GE} – greater or equal</li>
 *   <li>{@link Kind#LE} – less or equal</li>
 *   <li>{@link Kind#EQ} – equal</li>
 *   <li>{@link Kind#NE} – not equal</li>
 * </ul>
 *
 * <p>Example RPL: {@code >}</p>
 */
public final class CmpOp implements Instruction {

    /**
     * Kind of comparison.
     */
    public enum Kind {
        /** a {@literal >} b */ GT,
        /** a {@literal <} b */ LT,
        /** a {@literal >=} b */ GE,
        /** a {@literal <=} b */ LE,
        /** a == b */ EQ,
        /** a != b */ NE
    }

    /** Selected comparison kind. */
    public final Kind kind;

    /**
     * Creates a comparison operation IR node.
     * @param kind the comparison kind
     */
    public CmpOp(Kind kind) {
        this.kind = kind;
    }
}
