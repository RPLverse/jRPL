/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.parser;

import org.jrpl.compiler.ir.*;
import org.jrpl.compiler.lexer.*;

import java.util.ArrayList;
import java.util.List;

import static org.jrpl.compiler.lexer.TokenType.*;

/**
 * Minimal recursive-descent parser from tokens to IR.
 *
 * <p>Grammar:
 * <pre>
 * program := ( {@code <<} instr* {@code >>} )? EOF
 * instr   := NUMBER
 *         |  DUP | DROP | SWAP
 *         |  {@code +} | {@code -} | {@code *} | {@code /} | {@code ^}
 *         |  {@literal >} | {@literal <} | {@code >=} | {@code <=} | {@code ==} | {@code !=}
 *         |  IF THEN instr* (ELSE instr*)? END
 * </pre>
 *
 * <p>Notes:
 * <ul>
 *   <li>If {@code <<} is present, a matching {@code >>} is required.</li>
 *   <li>If {@code <<} is absent, a stray {@code >>} is rejected.</li>
 * </ul>
 */
public final class Parser {

    // Input token stream and current index
    private final List<Token> tokens;
    private int i = 0;

    /**
     * Creates a new parser over a token stream.
     *
     * @param tokens tokens produced by the lexer (must end with {@code EOF})
     */
    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Parses a complete program.
     *
     * @return the IR instruction sequence for the whole program
     * @throws IllegalArgumentException if the token stream is syntactically invalid
     */
    public List<Instruction> parseProgram() {

        // Track whether the program is delimited with '<<' ... '>>'
        boolean hasLShift = match(LSHIFT);

        List<Instruction> out = new ArrayList<>();

        // Parse instructions until '>>' or EOF
        while (!check(RSHIFT) && !check(EOF)) {
            out.add(parseInstr());
        }

        // Enforce delimiter pairing rules
        if (hasLShift) {
            // If program started with '<<', require a closing '>>'
            expect(RSHIFT, "'>>'");
        } else if (check(RSHIFT)) {
            // Found '>>' without a matching '<<'
            throw error("Unexpected '>>' without matching '<<'");
        }

        // End of input is always required
        expect(EOF, "end of input");
        return out;
    }

    // Parse a single instruction based on the current token
    private Instruction parseInstr() {

        // Dispatch based on current token type
        Token t = peek();
        switch (t.type()) {

            // Literals and stack ops
            case NUMBER -> { advance(); return new PushConst(t.value()); }
            case DUP    -> { advance(); return Dup.INSTANCE; }
            case DROP   -> { advance(); return Drop.INSTANCE; }
            case SWAP   -> { advance(); return Swap.INSTANCE; }

            // Arithmetic operators
            case PLUS   -> { advance(); return new BinOp(BinOp.Kind.ADD); }
            case MINUS  -> { advance(); return new BinOp(BinOp.Kind.SUB); }
            case STAR   -> { advance(); return new BinOp(BinOp.Kind.MUL); }
            case SLASH  -> { advance(); return new BinOp(BinOp.Kind.DIV); }
            case CARET  -> { advance(); return new BinOp(BinOp.Kind.POW); }

            // Comparison operators
            case GT     -> { advance(); return new CmpOp(CmpOp.Kind.GT); }
            case LT     -> { advance(); return new CmpOp(CmpOp.Kind.LT); }
            case GE     -> { advance(); return new CmpOp(CmpOp.Kind.GE); }
            case LE     -> { advance(); return new CmpOp(CmpOp.Kind.LE); }
            case EQ     -> { advance(); return new CmpOp(CmpOp.Kind.EQ); }
            case NE     -> { advance(); return new CmpOp(CmpOp.Kind.NE); }

            // Control flow
            case IF     -> { return parseIf(); }

            // Unexpected token
            default -> throw error("Unexpected token: " + t.lexeme());
        }
    }

    // Parse an IF...THEN...[ELSE]...END block
    private Instruction parseIf() {

        // Expect IF THEN
        expect(IF, "'IF'");
        expect(THEN, "'THEN'");

        // Parse THEN branch until ELSE or END
        List<Instruction> thenBranch = new ArrayList<>();
        while (!check(ELSE) && !check(END)) {
            thenBranch.add(parseInstr());
        }

        // Optionally parse ELSE branch
        List<Instruction> elseBranch = null;
        if (match(ELSE)) {
            elseBranch = new ArrayList<>();
            while (!check(END)) {
                elseBranch.add(parseInstr());
            }
        }

        // Expect END and build IfElse node
        expect(END, "'END'");
        return new IfElse(thenBranch, elseBranch);
    }

    // Look at current token without consuming it
    private Token peek() {
        return tokens.get(i);
    }

    // Check if current token matches expected type
    private boolean check(TokenType t) {
        return peek().type() == t;
    }

    // Consume current token and advance
    private Token advance() {
        return tokens.get(i++);
    }

    // Match and consume token if type matches
    private boolean match(TokenType t) {
        if (check(t)) {
            advance();
            return true;
        }
        return false;
    }

    // Expect specific token type or throw error
    private void expect(TokenType t, String ctx) {
        if (!check(t)) throw error("Expected " + ctx + " but found: " + peek().lexeme());
        advance();
    }

    // Create parser error
    private IllegalArgumentException error(String msg) {
        return new IllegalArgumentException(msg);
    }
}
