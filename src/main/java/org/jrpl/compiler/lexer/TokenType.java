/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.lexer;

/**
 * Kinds of lexical tokens produced by the minimal jRPL lexer.
 *
 * <p>This enumeration groups the token categories recognized by the lexer.
 * It is intentionally compact and tailored to the current subset of the language.
 */
public enum TokenType {

    /** Opening program delimiter {@code <<}. */
    LSHIFT,

    /** Closing program delimiter {@code >>}. */
    RSHIFT,

    /** Keyword {@code IF}. Begins a conditional construct. */
    IF,

    /** Keyword {@code THEN}. Starts the "then" branch of a conditional. */
    THEN,

    /** Keyword {@code ELSE}. Starts the optional "else" branch of a conditional. */
    ELSE,

    /** Keyword {@code END}. Closes a conditional construct. */
    END,

    /** Stack operation {@code DUP}: duplicate the top of stack. */
    DUP,

    /** Stack operation {@code DROP}: remove the top of stack. */
    DROP,

    /** Stack operation {@code SWAP}: swap the two topmost stack elements. */
    SWAP,

    /** Arithmetic operator {@code +} (addition). */
    PLUS,

    /** Arithmetic operator {@code -} (subtraction). */
    MINUS,

    /** Arithmetic operator {@code *} (multiplication). */
    STAR,

    /** Arithmetic operator {@code /} (division). */
    SLASH,

    /** Arithmetic operator {@code ^} (power). */
    CARET,

    /** Comparison operator {@literal >} (greater-than). */
    GT,

    /** Comparison operator {@literal <} (less-than). */
    LT,

    /** Comparison operator {@code >=} (greater-or-equal). */
    GE,

    /** Comparison operator {@code <=} (less-or-equal). */
    LE,

    /** Comparison operator {@code ==} (equal). */
    EQ,

    /** Comparison operator {@code !=} (not-equal). */
    NE,

    /** Numeric literal (integer or decimal), represented as a {@code double}. */
    NUMBER,

    /** End-of-input marker produced by the lexer. */
    EOF
}
