/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.lexer;

import org.jrpl.scan.Position;
import org.jrpl.scan.Span;

/**
 * Single lexical token produced by the {@link Lexer}.
 *
 * @param type token kind
 * @param lexeme raw text
 * @param value optional numeric value (for NUMBER), else NaN
 * @param span source span of the token
 */
public record Token(TokenType type, String lexeme, double value, Span span) {

    /**
     * Creates a non-numeric token (keyword, operator, delimiter).
     *
     * @param t   token type
     * @param s   lexeme string
     * @param sp  span in the source
     * @return a new {@code Token}
     */
    public static Token of(TokenType t, String s, Span sp) {
        return new Token(t, s, Double.NaN, sp);
    }

    /**
     * Creates a numeric token with its parsed {@code double} value.
     *
     * @param s   lexeme string (e.g. "66", "3.14")
     * @param v   numeric value parsed from the lexeme
     * @param sp  span in the source
     * @return a new {@code Token}
     */
    public static Token number(String s, double v, Span sp) {
        return new Token(TokenType.NUMBER, s, v, sp);
    }
}
