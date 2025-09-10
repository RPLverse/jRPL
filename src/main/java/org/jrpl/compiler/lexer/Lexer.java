/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.lexer;

import org.jrpl.scan.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal lexer for jRPL.
 *
 * <p>Recognizes:
 * <ul>
 *   <li>Delimiters: {@code <<} {@code >>}</li>
 *   <li>Keywords: {@code IF}, {@code THEN}, {@code ELSE}, {@code END}, {@code DUP}, {@code DROP}, {@code SWAP}</li>
 *   <li>Operators:
 *     {@code +}, {@code -}, {@code *}, {@code /}, {@code ^},
 *     {@literal >}, {@literal <}, {@code >=}, {@code <=}, {@code ==}, {@code !=}
 *   </li>
 *   <li>Numbers: integer and decimal</li>
 *   <li>Line comments starting with {@code ';'} until newline</li>
 * </ul>
 */
public final class Lexer {
    private final Source src;
    private final List<Token> tokens = new ArrayList<>();

    /**
     * Creates a new lexer over the given source string.
     *
     * @param source source code to tokenize
     */
    public Lexer(String source) {
        this.src = new Source(source);
    }

    /**
     * Lexes the entire input and returns the list of tokens,
     * always ending with an {@code EOF} token.
     *
     * @return the complete token list
     */
    public List<Token> lex() {
        while (!src.eof()) {
            char c = src.cursor();
            if (Character.isWhitespace(c)) {
                src.next();
            } else if (c == ';') { // comment
                while (!src.eof() && src.next() != '\n') {}
            } else if (Character.isDigit(c)) {
                tokens.add(lexNumber());
            } else if (c == '<' && src.match('<')) {
                tokens.add(Token.of(TokenType.LSHIFT, "<<", span(2)));
            } else if (c == '>' && src.match('>')) {
                tokens.add(Token.of(TokenType.RSHIFT, ">>", span(2)));
            } else {
                tokens.add(lexSymbolOrOperator());
            }
        }
        tokens.add(Token.of(TokenType.EOF, "", span(0)));
        return tokens;
    }

    private Token lexNumber() {
        Position start = src.position();
        while (!src.eof() && (Character.isDigit(src.cursor()) || src.cursor() == '.')) {
            src.next();
        }
        String text = src.slice(start);
        double value = Double.parseDouble(text);
        return Token.number(text, value, new Span(start, src.position()));
    }

    private Token lexSymbolOrOperator() {
        Position start = src.position();
        char c = src.next();
        switch (c) {
            case '+' -> { return Token.of(TokenType.PLUS, "+", span(1)); }
            case '-' -> { return Token.of(TokenType.MINUS, "-", span(1)); }
            case '*' -> { return Token.of(TokenType.STAR, "*", span(1)); }
            case '/' -> { return Token.of(TokenType.SLASH, "/", span(1)); }
            case '^' -> { return Token.of(TokenType.CARET, "^", span(1)); }
            case '>' -> {
                if (src.match('=')) return Token.of(TokenType.GE, ">=", span(2));
                else return Token.of(TokenType.GT, ">", span(1));
            }
            case '<' -> {
                if (src.match('=')) return Token.of(TokenType.LE, "<=", span(2));
                else return Token.of(TokenType.LT, "<", span(1));
            }
            case '=' -> {
                if (src.match('=')) return Token.of(TokenType.EQ, "==", span(2));
            }
            case '!' -> {
                if (src.match('=')) return Token.of(TokenType.NE, "!=", span(2));
            }
        }
        // identifier-like keywords
        while (!src.eof() && Character.isAlphabetic(src.cursor())) {
            src.next();
        }
        String word = src.slice(start).toUpperCase();
        return switch (word) {
            case "IF" -> Token.of(TokenType.IF, word, span(word.length()));
            case "THEN" -> Token.of(TokenType.THEN, word, span(word.length()));
            case "ELSE" -> Token.of(TokenType.ELSE, word, span(word.length()));
            case "END" -> Token.of(TokenType.END, word, span(word.length()));
            case "DUP" -> Token.of(TokenType.DUP, word, span(word.length()));
            case "DROP" -> Token.of(TokenType.DROP, word, span(word.length()));
            case "SWAP" -> Token.of(TokenType.SWAP, word, span(word.length()));
            default -> throw new IllegalArgumentException("Unknown token: " + word);
        };
    }

    private Span span(int length) {
        return new Span(new Position(src.position().index() - length, src.position().line(),
                src.position().column() - length), src.position());
    }
}
