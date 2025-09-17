/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.codegen;

import org.jrpl.compiler.ir.*;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

// Emits bytecode instructions for IR nodes (used by ClassEmitter)
// Translates each IR instruction into ASM calls on ExecStack, including
// arithmetic ops, comparisons, and structured control flow (IfElse)
final class IrEmitter {

    // Prevent instantiation
    private IrEmitter() {
    }

    // Emit a sequence of IR instructions using the given MethodVisitor
    // Used internally by ClassEmitter
    static void emit(List<Instruction> ir, MethodVisitor mv) {

        // Dispatch each IR instruction to the corresponding emit method
        for (Instruction i : ir) {
            if (i instanceof PushConst pc) emitPush(pc, mv);
            else if (i instanceof Dup) emitCall(mv, "dup", "()V");
            else if (i instanceof Drop) emitCall(mv, "drop", "()V");
            else if (i instanceof Swap) emitCall(mv, "swap", "()V");
            else if (i instanceof BinOp b) emitBin(b, mv);
            else if (i instanceof CmpOp c) emitCmp(c, mv);
            else if (i instanceof IfElse ie) emitIfElse(ie, mv);
            else throw new IllegalArgumentException("Unknown IR: " + i.getClass());
        }
    }

    // Push a constant double value onto the ExecStack
    private static void emitPush(PushConst pc, MethodVisitor mv) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(pc.value);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack", "push", "(D)V", false);
    }

    // Call void method on ExecStack (e.g., dup, drop, swap)
    private static void emitCall(MethodVisitor mv, String name, String desc) {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack", name, desc, false);
    }

    // Emit arithmetic operation (e.g., add, sub, mul, div, pow)
    private static void emitBin(BinOp b, MethodVisitor mv) {
        switch (b.kind) {
            case ADD -> emitCall(mv, "add", "()V");
            case SUB -> emitCall(mv, "sub", "()V");
            case MUL -> emitCall(mv, "mul", "()V");
            case DIV -> emitCall(mv, "div", "()V");
            case POW -> emitCall(mv, "pow", "()V");
        }
    }

    // Emit comparison operation (pushes 1.0 for true, 0.0 for false)
    private static void emitCmp(CmpOp c, MethodVisitor mv) {
        switch (c.kind) {
            case GT -> emitCall(mv, "cmpGT", "()V");
            case LT -> emitCall(mv, "cmpLT", "()V");
            case GE -> emitCall(mv, "cmpGE", "()V");
            case LE -> emitCall(mv, "cmpLE", "()V");
            case EQ -> emitCall(mv, "cmpEQ", "()V");
            case NE -> emitCall(mv, "cmpNE", "()V");
        }
    }

    // Emit conditional control flow for an IfElse instruction
    // Expects a boolean on top of the stack (1.0 = true, 0.0 = false),
    // which is popped and used as the branch condition
    private static void emitIfElse(IfElse ie, MethodVisitor mv) {

        // Pop condition (double), compare with 0.0 and push int (0 if false, 1 if true)
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack", "pop", "()D", false);
        mv.visitInsn(DCONST_0);
        mv.visitInsn(DCMPL);

        // Define branch labels
        Label elseL = new Label();
        Label endL  = new Label();

        // If/else branch emission
        if (ie.elseBranch != null) {

            // If zero (false) jump to elseL
            mv.visitJumpInsn(IFEQ, elseL);

            // Then branch
            emit(ie.thenBranch, mv);
            mv.visitJumpInsn(GOTO, endL);

            // Else branch
            mv.visitLabel(elseL);
            emit(ie.elseBranch, mv);
            mv.visitLabel(endL);

        // If-only branch
        } else {

            // If zero (false) jump to endL
            mv.visitJumpInsn(IFEQ, endL);
            emit(ie.thenBranch, mv);
            mv.visitLabel(endL);
        }
    }
}
