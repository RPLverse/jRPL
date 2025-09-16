/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import org.jrpl.compiler.Compiler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * jRPL command-line interface (CLI).
 *
 * <p>Reads a {@code .rpl} source, runs the compilation pipeline and writes a {@code .class} file.
 * <p>The generated class exposes:
 * <ul>
 *   <li>{@code public static void run(org.jrpl.runtime.ExecStack)}</li>
 *   <li>{@code public static void main(String[])} — accepts numeric args, prints top of stack (optional)</li>
 * </ul>
 *
 * <p>This class parses CLI arguments, performs file I/O and delegates
 * compilation to {@link org.jrpl.compiler.Compiler}.
 */
public final class Main {

    // Exit codes for abnormal termination
    private static final int EXIT_USAGE_ERROR = 1;
    private static final int EXIT_INVALID_INPUT = 2;

    // Prevent instantiation
    private Main() {
    }

    /**
     * Program entry point.
     *
     * <p>Usage: {@code jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>] [--no-main]}</p>
     *
     * @param args command-line arguments
     * @throws Exception if reading the input or writing the class fails
     */
    public static void main(String[] args) throws Exception {

        // Parse CLI options (validates flags and provides defaults)
        Options opt = Options.parse(args);

        // Validate input .rpl file
        Path input = opt.inputFile;
        if (!Files.exists(input) || !input.toString().endsWith(".rpl")) {
            System.err.println("Input must be an existing .rpl file: " + input);
            System.exit(EXIT_INVALID_INPUT);
        }

        // Read source file
        String source = Files.readString(input);

        // Generate class binary name for codegen with compact base-36 timestamp (e.g., "org.jrpl.gen.demo_kf3p9z")
        String classBinaryName = opt.classBinaryName != null ? opt.classBinaryName : autoName(input);
        String internal = classBinaryName.replace('.', '/');

        // Run the compilation pipeline (lex, parse, bytecode) via static facade
        byte[] bytes = Compiler.compile(source, internal, opt.withMain);

        // Write generated .class file to disk
        Path classFile = opt.outDir.resolve(internal + ".class");
        Path parent = classFile.getParent();
        if (parent != null) Files.createDirectories(parent);
        Files.write(classFile, bytes);

        // Print output summary
        System.out.println("Generated: " + classFile.toAbsolutePath());
        System.out.println("Class: " + classBinaryName);
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

    // Builds an auto-generated fully-qualified class name using input file name + base-36 timestamp
    private static String autoName(Path input) {
        String base = removeExtension(input.getFileName().toString());
        String ts = Long.toString(Instant.now().toEpochMilli(), 36);
        return "org.jrpl.gen." + sanitize(base) + "_" + ts;
    }
}
