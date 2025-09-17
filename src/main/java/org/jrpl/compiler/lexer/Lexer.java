/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.lexer;

import org.jrpl.scan.Position;
import org.jrpl.scan.Source;
import org.jrpl.scan.Span;

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

    /**
     * Creates a new lexer over the given source string.
     *
     * @param source source code to tokenize
     */
    public Lexer(String source) {
        this.src = new Source(source);
    }

    /**
     * Lexes the entire input and returns the list of tokens.
     *
     * <p>The returned list always ends with an {@code EOF} token.
     *
     * @return the complete token list
     */
    public List<Token> lex() {

        // Scan input until EOF, emitting tokens
        List<Token> out = new ArrayList<>();
        while (!src.eof()) {
            skipWhitespaceAndComments();
            if (src.eof()) break;

            // Mark token start and read current character
            Position start = src.position();
            char c = src.cursor();

            // Delimiters and multi-char operators starting with '<'
            if (c == '<') {
                src.next(); // consume first '<'
                if (src.match('<')) {
                    out.add(Token.of(TokenType.LSHIFT, "<<", span(start)));
                } else if (src.match('=')) {
                    out.add(Token.of(TokenType.LE, "<=", span(start)));
                } else {
                    out.add(Token.of(TokenType.LT, "<", span(start)));
                }
                continue;
            }

            // Delimiters and multi-char operators starting with '>'
            if (c == '>') {
                src.next(); // consume first '>'
                if (src.match('>')) {
                    out.add(Token.of(TokenType.RSHIFT, ">>", span(start)));
                } else if (src.match('=')) {
                    out.add(Token.of(TokenType.GE, ">=", span(start)));
                } else {
                    out.add(Token.of(TokenType.GT, ">", span(start)));
                }
                continue;
            }

            // Equality
            if (c == '=') {
                src.next();
                if (src.match('=')) {
                    out.add(Token.of(TokenType.EQ, "==", span(start)));
                } else {
                    throw error("Unexpected '='; did you mean '=='?", start);
                }
                continue;
            }

            // Inequality
            if (c == '!') {
                src.next();
                if (src.match('=')) {
                    out.add(Token.of(TokenType.NE, "!=", span(start)));
                } else {
                    throw error("Unexpected '!'; did you mean '!='?", start);
                }
                continue;
            }

            // Single-char operators
            if (c == '+') { src.next(); out.add(Token.of(TokenType.PLUS, "+", span(start))); continue; }
            if (c == '-') { src.next(); out.add(Token.of(TokenType.MINUS, "-", span(start))); continue; }
            if (c == '*') { src.next(); out.add(Token.of(TokenType.STAR,  "*", span(start))); continue; }
            if (c == '/') { src.next(); out.add(Token.of(TokenType.SLASH, "/", span(start))); continue; }
            if (c == '^') { src.next(); out.add(Token.of(TokenType.CARET, "^", span(start))); continue; }

            // Numbers
            if (Character.isDigit(c)) {
                out.add(readNumber(start));
                continue;
            }

            // Identifiers (keywords)
            if (Character.isLetter(c)) {
                out.add(readKeyword(start));
                continue;
            }

            // Unexpected character
            throw error("Unexpected character: '" + c + "'", start);
        }

        // Always append EOF
        out.add(Token.of(TokenType.EOF, "", span(src.position())));
        return out;
    }

    // Skip whitespace and ';' line comments
    private void skipWhitespaceAndComments() {
        while (!src.eof()) {
            char c = src.cursor();
            if (Character.isWhitespace(c)) { src.next(); continue; }
            if (c == ';') { // comment until newline
                while (!src.eof() && src.next() != '\n') { /* skip */ }
                continue;
            }
            break;
        }
    }

    // Read a NUMBER token (integer or decimal)
    private Token readNumber(Position start) {
        boolean sawDot = false;
        while (!src.eof()) {
            char ch = src.cursor();
            if (Character.isDigit(ch)) {
                src.next();
            } else if (ch == '.' && !sawDot) {
                sawDot = true;
                src.next();
            } else {
                break;
            }
        }
        String lexeme = src.slice(start);
        double value = Double.parseDouble(lexeme);
        return Token.number(lexeme, value, new Span(start, src.position()));
    }

    // Read an identifier and classify it as a keyword
    private Token readKeyword(Position start) {
        while (!src.eof()) {
            char ch = src.cursor();
            if (Character.isLetterOrDigit(ch) || ch == '_') {
                src.next();
            } else break;
        }
        String word = src.slice(start);
        String upper = word.toUpperCase();
        TokenType tt = switch (upper) {
            case "IF"   -> TokenType.IF;
            case "THEN" -> TokenType.THEN;
            case "ELSE" -> TokenType.ELSE;
            case "END"  -> TokenType.END;
            case "DUP"  -> TokenType.DUP;
            case "DROP" -> TokenType.DROP;
            case "SWAP" -> TokenType.SWAP;
            default -> throw error("Unknown identifier: " + word, start);
        };
        return Token.of(tt, word, new Span(start, src.position()));
    }

    // Create a span from a start position to the current cursor
    private Span span(Position start) {
        return new Span(start, src.position());
    }

    // Build an error with message and position
    private IllegalArgumentException error(String msg, Position at) {
        return new IllegalArgumentException(msg + " at " + at);
    }
}
