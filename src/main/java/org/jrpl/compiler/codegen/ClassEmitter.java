/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.codegen;

import org.jrpl.compiler.ir.Instruction;
import org.objectweb.asm.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * Generates a bytecode class from a list of IR instructions.
 *
 * <p>The generated class contains:
 * <ul>
 *   <li>{@code public static void run(org.jrpl.runtime.ExecStack)}</li>
 *   <li>(optional) {@code public static void main(String[])} for direct execution</li>
 * </ul>
 */
public final class ClassEmitter {

    // ASM internal class name (e.g., "org/jrpl/gen/Demo") and flag to generate main(String[])
    private final String internalClassName;
    private final boolean withMain;

    /**
     * Creates a new class emitter for the given internal bytecode name.
     *
     * @param internalClassName the bytecode-internal name (e.g., "org/jrpl/gen/demo")
     */
    public ClassEmitter(String internalClassName) {

        // Generate main(String[]) when not specified
        this(internalClassName, true);
    }

    /**
     * Creates a new class emitter with an option to generate a {@code main} entry point.
     *
     * @param internalClassName internal bytecode name (e.g., {@code "org/jrpl/gen/Demo"})
     * @param withMain whether to generate a public static void main(String[])
     */
    public ClassEmitter(String internalClassName, boolean withMain) {

        // Initialize fields
        this.internalClassName = internalClassName;
        this.withMain = withMain;
    }

    /**
     * Emits a new bytecode class in memory from the given IR instructions.
     *
     * @param ir list of IR instructions to compile
     * @return compiled class bytes
     */
    public byte[] emit(List<Instruction> ir) {

        // Setup ClassWriter and start class definition (Java 17, extends Object)
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cw.visit(V17, ACC_PUBLIC | ACC_SUPER, internalClassName, null, "java/lang/Object", null);

        // Public no-arg constructor equivalent to "public Demo() { super(); }"
        MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(0, 0);
        ctor.visitEnd();

        // Define public static void run(ExecStack) and emit IR
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "run",
                "(Lorg/jrpl/runtime/ExecStack;)V", null, null);
        mv.visitCode();
        IrEmitter.emit(ir, mv);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();

        // Add optional main(String[]) if requested
        if (withMain) {
            emitMain(cw);
        }

        // Finalize class and return bytecode
        cw.visitEnd();
        return cw.toByteArray();
    }

    /**
     * Emits a new bytecode class and writes the bytecode to a file.
     *
     * @param out path where the .class file will be written
     * @param ir  list of IR instructions to compile
     * @throws Exception if writing to the file system fails
     */
    public void writeTo(Path out, List<Instruction> ir) throws Exception {

        // Emit bytecode from IR and write .class file to disk
        byte[] bytes = emit(ir);
        Files.createDirectories(out.getParent());
        Files.write(out, bytes);
    }

    /**
     * Emits the {@code public static void main(String[] args)} method.
     *
     * <p>Equivalent Java code generated:
     * <pre>{@code
     * ExecStack s = new ExecStack();
     * for (int i = 0; i < args.length; i++) {
     *     s.push(Double.parseDouble(args[i]));
     * }
     * run(s);
     * if (s.size() > 0) {
     *     System.out.println(s.pop());
     * } else {
     *     System.out.println("Stack empty");
     * }
     * }</pre>
     *
     * @param cw the ASM class writer where the method will be emitted
     */
    private void emitMain(ClassWriter cw) {

        // Define public static void main(String[])
        MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mv.visitCode();

        // Create new ExecStack s
        mv.visitTypeInsn(NEW, "org/jrpl/runtime/ExecStack");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "org/jrpl/runtime/ExecStack", "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 1);

        // Initialize int i = 0;
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);

        // Define loop start/end labels and mark loopStart
        Label loopStart = new Label();
        Label loopEnd   = new Label();
        mv.visitLabel(loopStart);

        // if (i >= args.length) goto loopEnd;
        mv.visitVarInsn(ILOAD, 2);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitInsn(ARRAYLENGTH);
        Label body = new Label();
        mv.visitJumpInsn(IF_ICMPLT, body);
        mv.visitJumpInsn(GOTO, loopEnd);

        // Loop body: s.push(Double.parseDouble(args[i]))
        mv.visitLabel(body);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(AALOAD);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Double",
                "parseDouble", "(Ljava/lang/String;)D", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack",
                "push", "(D)V", false);

        // Increment loop counter i
        mv.visitIincInsn(2, 1);
        mv.visitJumpInsn(GOTO, loopStart);

        // loopEnd:
        mv.visitLabel(loopEnd);

        // Call run(s);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESTATIC, internalClassName,
                "run", "(Lorg/jrpl/runtime/ExecStack;)V", false);

        // Print result: if (s.size() > 0) println(pop()) else println("Stack empty")
        Label hasItems = new Label();
        Label done     = new Label();
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack",
                "size", "()I", false);
        mv.visitJumpInsn(IFGT, hasItems);

        // Else branch: System.out.println("Stack empty")
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("Stack empty");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(Ljava/lang/String;)V", false);
        mv.visitJumpInsn(GOTO, done);

        // Then branch: System.out.println(s.pop())
        mv.visitLabel(hasItems);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "org/jrpl/runtime/ExecStack",
                "pop", "()D", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream",
                "println", "(D)V", false);

        // Mark end label and finalize main()
        mv.visitLabel(done);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
