/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Removes the top element of the stack.
 *
 * <p>Equivalent to the RPL instruction {@code DROP}.</p>
 *
 * <p>Stack effect:
 * <pre>
 *   before: [a]
 *   after : []
 * </pre>
 */
public final class Drop implements Instruction {

    /**
     * Singleton instance (stateless).
     */
    public static final Drop INSTANCE = new Drop();

    // Prevent external instantiation
    private Drop() {
    }
}
