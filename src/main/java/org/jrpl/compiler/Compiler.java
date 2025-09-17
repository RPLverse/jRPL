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

import java.util.List;

/**
 * Entry point for the jRPL compilation pipeline.
 *
 * <p>Transforms RPL source code into bytecode by performing:
 * <ul>
 *   <li>Lexical analysis ({@link Lexer})</li>
 *   <li>Parsing into IR ({@link Parser})</li>
 *   <li>Bytecode generation ({@link ClassEmitter})</li>
 * </ul>
 *
 * <p>This class is not instantiable, all functionality is exposed through static methods.
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

        // Lex source, parse into IR and generate bytecode
        List<Token> tokens = new Lexer(source).lex();
        List<Instruction> ir = new Parser(tokens).parseProgram();
        return new ClassEmitter(internalClassName, withMain).emit(ir);
    }
}
