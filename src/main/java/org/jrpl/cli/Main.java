/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * jRPL command-line interface (CLI).
 *
 * <p>This class provides the entry point for the compiler.
 * It accepts an {@code .rpl} source file, and generates an equivalent bytecode {@code .class} file
 * Currently, the generated class is a no-op bytecode containing:
 * <ul>
 *   <li>a default constructor</li>
 *   <li>a public static {@code run()} method with an empty body</li>
 *   <li>a public static {@code main(String[])} method delegating to {@code run()}</li>
 * </ul>
 * This no-op bytecode will be replaced with the full RPL to bytecode compiler.
 */
public final class Main implements Opcodes {

    /**
     * Exit code used when the program arguments are invalid or missing.
     */
    private static final int EXIT_USAGE_ERROR = 1;

    /**
     * Exit code used when the input .rpl file is invalid or missing.
     */
    private static final int EXIT_INVALID_INPUT = 2;

    /**
     * Hidden constructor to prevent instantiation.
     */
    private Main() {

        // No instances
    }

    /**
     * Program entry point.
     *
     * @param args command-line arguments:
     * <ul>
     *   <li>{@code <file.rpl>} — input file to compile</li>
     *   <li>{@code --out-dir <dir>} — (optional) output directory for .class file</li>
     *   <li>{@code --class-name <BinaryName>} — (optional) fully qualified binary class name</li>
     * </ul>
     * @throws Exception if reading the file or writing the class fails
     */
    public static void main(String[] args) throws Exception {

        // Arguments validation
        if (args.length == 0) {
            System.out.println("Usage: jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>]");
            System.exit(EXIT_USAGE_ERROR);
        }

        // Input file validation
        Path input = Path.of(args[0]);
        if (!Files.exists(input) || !input.toString().endsWith(".rpl")) {
            System.err.println("Input must be an existing .rpl file: " + input);
            System.exit(EXIT_INVALID_INPUT);
        }

        // Optional flags
        Path outDir = Path.of("build/gen-classes");
        String classBinaryName = null;
        for (int i = 1; i < args.length; i++) {
            switch (args[i]) {
                case "--out-dir" -> {
                    if (i + 1 >= args.length) die("--out-dir requires a value", EXIT_USAGE_ERROR);
                    outDir = Path.of(args[++i]);
                }
                case "--class-name" -> {
                    if (i + 1 >= args.length) die("--class-name requires a value", EXIT_USAGE_ERROR);
                    classBinaryName = args[++i];
                }
                default -> { /* ignore unknown flags for now */ }
            }
        }

        // Read the .rpl (currently unused, parsing will be implemented later)
        String s = Files.readString(input);

        // Derive class name if not provided: org.jrpl.gen.<BaseName>_<timestamp>
        if (classBinaryName == null) {
            String base = removeExtension(input.getFileName().toString());
            String ts = Long.toString(Instant.now().toEpochMilli(), 36);
            classBinaryName = "org.jrpl.gen." + sanitize(base) + "_" + ts;
        }

        // Generate a minimal, valid .class with a public static void run() {} no-op
        byte[] bytes = generateNoOpClass(classBinaryName);

        // Write to disk: <outDir>/<binaryName as path>.class
        Path classFile = outDir.resolve(classBinaryName.replace('.', '/') + ".class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, bytes);

        // Inform the user about the result of the compilation
        System.out.println("Generated: " + classFile.toAbsolutePath());
        System.out.println("Class: " + classBinaryName);
    }

    /**
     * Utility method to print the error message to standard error and exit with an exitCode.
     *
     * @param msg error message to print
     * @param exitCode process exit code to use
     */
    private static void die(String msg, int exitCode) {

        // Print the error message and exit
        System.err.println(msg);
        System.exit(exitCode);
    }

    /**
     * Remove file extension from a file name.
     *
     * @param name file name, e.g. {@code "hello.rpl"}
     * @return the base name without extension, e.g. {@code "hello"}
     */
    private static String removeExtension(String name) {

        // Find the last '.' in the filename and remove the extension
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(0, i) : name;
    }

    /**
     * Sanitize a string to be usable as a Java identifier fragment.
     * <p>Replaces non-alphanumeric characters with underscores and ensures
     * the first character is a valid identifier start.
     *
     * @param s input string
     * @return a sanitized identifier fragment
     */
    private static String sanitize(String s) {

        // Replace every non-alphanumeric/underscore character with '_'
        String t = s.replaceAll("[^A-Za-z0-9_]", "_");

        // If the first character is not a valid Java identifier start, prefix an underscore
        if (!t.isEmpty() && Character.isJavaIdentifierStart(t.charAt(0))) return t;
        return "_" + t;
    }

    /**
     * Generate a minimal no-op bytecode class using ASM.
     * The generated class has the following structure:
     * <ul>
     *   <li>a public default constructor that calls {@code super()}</li>
     *   <li>a public static {@code void run()} method with an empty body (no-op)</li>
     *   <li>a public static {@code void main(String[] args)} method that delegates to {@code run()}</li>
     * </ul>
     * Bytecode details:
     * <ul>
     *   <li>{@link Opcodes#ALOAD} and {@link Opcodes#INVOKESPECIAL} are used to invoke the superclass constructor</li>
     *   <li>{@link Opcodes#RETURN} marks the end of each method</li>
     *   <li>{@link ClassWriter#COMPUTE_FRAMES} and {@link ClassWriter#COMPUTE_MAXS} are enabled,
     *       so ASM computes stack frames and limits automatically</li>
     * </ul>
     * @param binaryName fully qualified binary name, e.g. {@code org.jrpl.gen.Test}
     * @return the byte array representing the class file
     */
    private static byte[] generateNoOpClass(String binaryName) {

        // Convert the binary class name, e.g. "org.jrpl.gen.Demo" in "org/jrpl/gen/Demo"
        String internal = binaryName.replace('.', '/');

        // Create a ClassWriter asking ASM to compute stack map frames, max stack and max locals 
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        // Begin the class definition (targeting Java 17 classfile; runs on 17+ including 21)
        cw.visit(V17, ACC_PUBLIC | ACC_FINAL, internal, null, "java/lang/Object", null);

        // Default constructor
        MethodVisitor ctor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        ctor.visitCode();
        ctor.visitVarInsn(ALOAD, 0);
        ctor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        ctor.visitInsn(RETURN);
        ctor.visitMaxs(0, 0);
        ctor.visitEnd();

        // Public static void run() {}
        MethodVisitor run = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "run", "()V", null, null);
        run.visitCode();
        run.visitInsn(RETURN);
        run.visitMaxs(0, 0);
        run.visitEnd();

        // Public static void main(String[] args) { run(); }
        MethodVisitor main = cw.visitMethod(ACC_PUBLIC | ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        main.visitCode();

        // Call run()
        main.visitMethodInsn(INVOKESTATIC, internal, "run", "()V", false);
        main.visitInsn(RETURN);
        main.visitMaxs(0, 0);
        main.visitEnd();

        // Finalize the class definition and return the generated bytecode
        cw.visitEnd();
        return cw.toByteArray();
    }
}
