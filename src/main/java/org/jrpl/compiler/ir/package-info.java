/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

/**
 * Intermediate Representation (IR) for jRPL.
 *
 * <p>Defines small immutable nodes mirroring RPL semantics.
 * <p>The IR is stack-based and shared by interpreter/codegen pipelines.
 *
 * @see org.jrpl.compiler.ir.Instruction
 * @see org.jrpl.compiler.ir.PushConst
 * @see org.jrpl.compiler.ir.BinOp
 * @see org.jrpl.compiler.ir.CmpOp
 * @see org.jrpl.compiler.ir.IfElse
 */
package org.jrpl.compiler.ir;
