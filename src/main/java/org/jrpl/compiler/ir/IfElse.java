/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

import java.util.List;

/**
 * Conditional execution of two branches depending on a boolean value.
 *
 * <p>The runtime stack is expected to contain a value on top, interpreted
 * as {@code 0.0 = false}, {@code 1.0 = true}. This value is popped and used
 * as the branch condition.
 *
 * <h2>Semantics:</h2>
 * <ul>
 *   <li>If the condition is true execute {@link #thenBranch}.</li>
 *   <li>If the condition is false execute {@link #elseBranch} (if present).</li>
 * </ul>
 *
 * <p>Example RPL:</p>
 * <pre>
 *   IF THEN ... ELSE ... END
 * </pre>
 */
public final class IfElse implements Instruction {

    /** Instructions executed if condition is true. */
    public final List<Instruction> thenBranch;

    /** Instructions executed if condition is false (may be null). */
    public final List<Instruction> elseBranch;

    /**
     * Creates a conditional IR node with an optional else branch.
     *
     * @param thenBranch instructions executed when condition is true
     * @param elseBranch instructions executed when condition is false (nullable)
     */
    public IfElse(List<Instruction> thenBranch, List<Instruction> elseBranch) {
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
