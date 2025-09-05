/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.ir;

/**
 * Marker interface for all intermediate representation (IR) instructions.
 * 
 * <p>The IR is designed to be stack-based, closely mirroring the semantics
 * of RPL and the runtime stack {@code S}.
 *
 * <h2>Design notes:</h2>
 * <ul>
 *   <li>Each IR node is immutable.</li>
 *   <li>IR nodes are kept minimal to simplify code generation.</li>
 *   <li>Interpretation and codegen can share the same IR tree.</li>
 * </ul>
 */
public interface Instruction {
}
