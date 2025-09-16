/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler.lexer;

/**
 * Enumeration of all token kinds recognized by the lexer.
 *
 * <p>Includes keywords, operators, delimiters, numbers and EOF.
 */
public enum TokenType {

    /** 
     * Delimiter: {@code <<} 
     */ 
    LSHIFT,

    /** 
     * Delimiter: {@code >>} 
     */ 
    RSHIFT,

    /** 
     * Keyword: {@code IF} 
     */ 
    IF,

    /** 
     * Keyword: {@code THEN} 
     */ 
    THEN,

    /** 
     * Keyword: {@code ELSE} 
     */ 
    ELSE,

    /** 
     * Keyword: {@code END} 
     */ 
    END,

    /** 
     * Keyword: {@code DUP} 
     */ 
    DUP,

    /** 
     * Keyword: {@code DROP} 
     */ 
    DROP,

    /** 
     * Keyword: {@code SWAP} 
     */ 
    SWAP,

    /** 
     * Operator: {@code +} 
     */ 
    PLUS,

    /** 
     * Operator: {@code -} 
     */ 
    MINUS,

    /** 
     * Operator: {@code *} 
     */ 
    STAR,

    /** 
     * Operator: {@code /} 
     */ 
    SLASH,

    /** 
     * Operator: {@code ^} 
     */ 
    CARET,

    /** 
     * Operator: {@code >} 
     */ 
    GT,

    /** 
     * Operator: {@code <} 
     */ 
    LT,

    /** 
     * Operator: {@code >=} 
     */ 
    GE,

    /** 
     * Operator: {@code <=} 
     */ 
    LE,

    /** 
     * Operator: {@code ==} 
     */ 
    EQ,

    /** 
     * Operator: {@code !=} 
     */ 
    NE,

    /** 
     * Numeric literal 
     */ 
    NUMBER,

    /** 
     * End of file 
     */ 
    EOF
}
