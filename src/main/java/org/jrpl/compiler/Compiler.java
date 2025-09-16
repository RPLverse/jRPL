/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler;

import org.jrpl.compiler.codegen.ClassEmitter;
import org.jrpl.compiler.ir.Instruction;
import org.jrpl.compiler.lexer.Lexer;
import org.jrpl.compiler.lexer.Token;
import org.jrpl.compiler.parser.Parser;

import java.nio.file.Path;
import java.util.List;

/**
 * Facade for the jRPL compilation pipeline.
 *
 * <p>Transforms RPL source code into bytecode by performing:</p>
 * <ul>
 *   <li>Lexical analysis ({@link Lexer})</li>
 *   <li>Parsing into IR ({@link Parser})</li>
 *   <li>Bytecode generation ({@link ClassEmitter})</li>
 * </ul>
 *
 * <p>This class is not instantiable, all functionality is exposed through static methods.</p>
 */
public final class Compiler {

    // Prevent instantiation
    private Compiler() {
    }

    /**
     * Compiles RPL source code into a bytecode class file.
     *
     * @param source the RPL source code
     * @param internalClassName bytecode-internal class name (e.g. {@code org/jrpl/gen/Demo})
     * @param withMain whether to generate a {@code public static void main(String[])} method
     * @return compiled class bytes
     */
    public static byte[] compile(String source, String internalClassName, boolean withMain) {
        List<Token> tokens = new Lexer(source).lex();
        List<Instruction> ir = new Parser(tokens).parseProgram();
        return new ClassEmitter(internalClassName, withMain).emit(ir);
    }

    /**
     * Compiles RPL source code into a bytecode class file and writes it to disk.
     *
     * @param source the RPL source code
     * @param internalClassName JVM-internal class name (e.g. {@code org/jrpl/gen/Demo})
     * @param withMain whether to generate a {@code public static void main(String[])} method
     * @param out path where the .class file will be written
     * @throws Exception if writing to the file system fails
     */
    public static void compileToFile(
            String source, String internalClassName, boolean withMain, Path out) throws Exception {
        List<Token> tokens = new Lexer(source).lex();
        List<Instruction> ir = new Parser(tokens).parseProgram();
        new ClassEmitter(internalClassName, withMain).writeTo(out, ir);
    }
}
