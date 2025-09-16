/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

/**
 * Bytecode generation for jRPL.
 *
 * <p>Transforms the intermediate representation (IR) into JVM bytecode using
 * ASM.
 * <p>Exposes a public API to emit a class and an internal helper that emits
 * method bodies.
 *
 * @see org.jrpl.compiler.codegen.ClassEmitter
 */
package org.jrpl.compiler.codegen;
