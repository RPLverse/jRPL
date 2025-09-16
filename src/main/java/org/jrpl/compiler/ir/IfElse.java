/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

import java.util.List;

/**
 * Conditional execution based on a boolean (1.0 = true, 0.0 = false) popped from the stack.
 *
 * <p>Executes {@link #thenBranch} when true; executes {@link #elseBranch} when false (if present).</p>
 *
 * <p>Example RPL:
 * <pre>{@code
 *  IF THEN
 *    66
 *  ELSE
 *    0
 *  END
 * }</pre>
 */
public final class IfElse implements Instruction {

    /**
     * Instructions executed when the condition is true.
     */
    public final List<Instruction> thenBranch;

    /**
     * Instructions executed when the condition is false (may be {@code null}).
     */
    public final List<Instruction> elseBranch;

    /**
     * Creates a new conditional IR node with an optional else-branch.
     *
     * @param thenBranch instructions executed when the condition is true
     * @param elseBranch instructions executed when the condition is false (nullable)
     */
    public IfElse(List<Instruction> thenBranch, List<Instruction> elseBranch) {
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
