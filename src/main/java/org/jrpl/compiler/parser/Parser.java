/**
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
 * Minimal recursive-descent parser from tokens to jRPL IR.
 *
 * <p>Grammar (informal):
 * <pre>
 * program := ({@code <<})? instr* ({@code >>})? EOF
 * instr   := NUMBER
 *         |  DUP | DROP | SWAP
 *         |  {@code +} | {@code -} | {@code *} | {@code /} | {@code ^}
 *         |  {@literal >} | {@literal <} | {@code >=} | {@code <=} | {@code ==} | {@code !=}
 *         |  IF THEN instr* (ELSE instr*)? END
 * </pre>
 */
public final class Parser {
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
        // optional '<<'
        if (match(LSHIFT)) { /* consume */ }
        List<Instruction> out = new ArrayList<>();
        while (!check(RSHIFT) && !check(EOF)) {
            out.add(parseInstr());
        }
        // optional '>>'
        if (match(RSHIFT)) { /* consume */ }
        expect(EOF, "end of input");
        return out;
    }

    private Instruction parseInstr() {
        Token t = peek();
        switch (t.type()) {
            case NUMBER -> { advance(); return new PushConst(t.value()); }
            case DUP    -> { advance(); return Dup.INSTANCE; }
            case DROP   -> { advance(); return Drop.INSTANCE; }
            case SWAP   -> { advance(); return Swap.INSTANCE; }

            case PLUS   -> { advance(); return new BinOp(BinOp.Kind.ADD); }
            case MINUS  -> { advance(); return new BinOp(BinOp.Kind.SUB); }
            case STAR   -> { advance(); return new BinOp(BinOp.Kind.MUL); }
            case SLASH  -> { advance(); return new BinOp(BinOp.Kind.DIV); }
            case CARET  -> { advance(); return new BinOp(BinOp.Kind.POW); }

            case GT     -> { advance(); return new CmpOp(CmpOp.Kind.GT); }
            case LT     -> { advance(); return new CmpOp(CmpOp.Kind.LT); }
            case GE     -> { advance(); return new CmpOp(CmpOp.Kind.GE); }
            case LE     -> { advance(); return new CmpOp(CmpOp.Kind.LE); }
            case EQ     -> { advance(); return new CmpOp(CmpOp.Kind.EQ); }
            case NE     -> { advance(); return new CmpOp(CmpOp.Kind.NE); }

            case IF     -> { return parseIf(); }

            default -> throw error("Unexpected token: " + t.lexeme());
        }
    }

    private Instruction parseIf() {
        expect(IF, "'IF'");
        expect(THEN, "'THEN'");
        List<Instruction> thenBranch = new ArrayList<>();
        while (!check(ELSE) && !check(END)) {
            thenBranch.add(parseInstr());
        }
        List<Instruction> elseBranch = null;
        if (match(ELSE)) {
            elseBranch = new ArrayList<>();
            while (!check(END)) {
                elseBranch.add(parseInstr());
            }
        }
        expect(END, "'END'");
        return new IfElse(thenBranch, elseBranch);
    }

    // helpers
    private Token peek() { return tokens.get(i); }
    private boolean check(TokenType t) { return peek().type() == t; }
    private Token advance() { return tokens.get(i++); }
    private boolean match(TokenType t) { if (check(t)) { advance(); return true; } return false; }
    private void expect(TokenType t, String ctx) {
        if (!check(t)) throw error("Expected " + ctx + " but found: " + peek().lexeme());
        advance();
    }
    private IllegalArgumentException error(String msg) { return new IllegalArgumentException(msg); }
}
