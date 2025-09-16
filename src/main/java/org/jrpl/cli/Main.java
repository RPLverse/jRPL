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
 * <p>Compiles a minimal subset of RPL from a {@code .rpl} source to a runnable {@code .class}.
 * <p>The generated class exposes:
 * <ul>
 *   <li>{@code public static void run(org.jrpl.runtime.ExecStack)}</li>
 *   <li>{@code public static void main(String[])} — accepts numeric args, prints top of stack</li>
 * </ul>
 */
public final class Main implements Opcodes {

    // Exit codes for abnormal termination
    private static final int EXIT_USAGE_ERROR = 1;
    private static final int EXIT_INVALID_INPUT = 2;

    // Prevent instantiation
    private Main() {
    }

    /**
     * Program entry point.
     *
     * <p>Usage: {@code jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>]}.
     * <p>Compiles the given RPL source into a {@code .class} with
     * {@code run(org.jrpl.runtime.ExecStack)} and {@code main(String[])}.
     *
     * @param args command-line arguments
     * @throws Exception if reading the input or writing the class fails
     */
    public static void main(String[] args) throws Exception {

        // Validate CLI arguments
        if (args.length == 0) {
            System.out.println("Usage: jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>]");
            System.exit(EXIT_USAGE_ERROR);
        }

        // Validate input .rpl file
        Path input = Path.of(args[0]);
        if (!Files.exists(input) || !input.toString().endsWith(".rpl")) {
            System.err.println("Input must be an existing .rpl file: " + input);
            System.exit(EXIT_INVALID_INPUT);
        }

        // Parse optional CLI flags (--out-dir, --class-name)
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

        // Read source file
        String source = Files.readString(input);

        // Perform lexical analysis
        List<Token> tokens = new Lexer(source).lex();

        // Parse tokens into IR
        List<Instruction> ir = new Parser(tokens).parseProgram();

        // Generate class name for codegen with compact base-36 timestamp (e.g., "org.jrpl.gen.demo_kf3p9z")
        if (classBinaryName == null) {
            String base = removeExtension(input.getFileName().toString());
            String ts = Long.toString(Instant.now().toEpochMilli(), 36);
            classBinaryName = "org.jrpl.gen." + sanitize(base) + "_" + ts;
        }
        String internal = classBinaryName.replace('.', '/');
        byte[] bytes = new ClassEmitter(internal, true).emit(ir);

        // Write generated class file
        Path classFile = outDir.resolve(internal + ".class");
        Files.createDirectories(classFile.getParent());
        Files.write(classFile, bytes);

        // Print output summary
        System.out.println("Generated: " + classFile.toAbsolutePath());
        System.out.println("Class: " + classBinaryName);
    }

    // Print error message and exit with the given code
    private static void die(String msg, int exitCode) {
        System.err.println(msg);
        System.exit(exitCode);
    }

    // Remove the last file extension (e.g., from "foo.rpl" to "foo")
    private static String removeExtension(String name) {
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(0, i) : name;
    }

    // Replace non-alphanumeric chars with '_' and prefix '_' if start is invalid
    private static String sanitize(String s) {
        String t = s.replaceAll("[^A-Za-z0-9_]", "_");
        if (!t.isEmpty() && Character.isJavaIdentifierStart(t.charAt(0))) return t;
        return "_" + t;
    }
}
