/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import org.jrpl.compiler.codegen.ClassEmitter;
import org.jrpl.compiler.ir.Instruction;
import org.jrpl.compiler.lexer.Lexer;
import org.jrpl.compiler.lexer.Token;
import org.jrpl.compiler.parser.Parser;
import org.objectweb.asm.Opcodes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

/**
 * jRPL command-line interface (CLI).
 *
 * <p>Compiles a minimal subset of RPL from {@code .rpl} to a runnable {@code .class}.
 * The generated class exposes:
 * <ul>
 *   <li>{@code public static void run(org.jrpl.runtime.ExecStack)}</li>
 *   <li>{@code public static void main(String[])} — accepts numeric args, prints top of stack</li>
 * </ul>
 */
public final class Main implements Opcodes {
    private static final int EXIT_USAGE_ERROR = 1;
    private static final int EXIT_INVALID_INPUT = 2;

    private Main() { }

    /**
     * Program entry point.
     *
     * <p>Usage: {@code jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>]}.
     * Compiles the given RPL source into a {@code .class} with
     * {@code run(org.jrpl.runtime.ExecStack)} and {@code main(String[])}.
     *
     * @param args command-line arguments
     * @throws Exception if reading the input or writing the class fails
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>]");
            System.exit(EXIT_USAGE_ERROR);
        }

        Path input = Path.of(args[0]);
        if (!Files.exists(input) || !input.toString().endsWith(".rpl")) {
            System.err.println("Input must be an existing .rpl file: " + input);
            System.exit(EXIT_INVALID_INPUT);
        }

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

        // Read source and compile
        String source = Files.readString(input);

        // 1) lex
        List<Token> tokens = new Lexer(source).lex();

        // 2) parse → IR
        List<Instruction> ir = new Parser(tokens).parseProgram();

        // 3) codegen
        if (classBinaryName == null) {
            String base = removeExtension(input.getFileName().toString());
            String ts = Long.toString(Instant.now().toEpochMilli(), 36);
            classBinaryName = "org.jrpl.gen." + sanitize(base) + "_" + ts;
        }
        String internal = classBinaryName.replace('.', '/');
        byte[] bytes = new ClassEmitter(internal, true).emit(ir);

        // 4) write
        Path classFile = outDir.resolve(internal + ".class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, bytes);

        System.out.println("Generated: " + classFile.toAbsolutePath());
        System.out.println("Class: " + classBinaryName);
    }

    private static void die(String msg, int exitCode) { System.err.println(msg); System.exit(exitCode); }
    private static String removeExtension(String name) { int i = name.lastIndexOf('.'); return i >= 0 ? name.substring(0, i) : name; }
    private static String sanitize(String s) {
        String t = s.replaceAll("[^A-Za-z0-9_]", "_");
        if (!t.isEmpty() && Character.isJavaIdentifierStart(t.charAt(0))) return t;
        return "_" + t;
    }
}
