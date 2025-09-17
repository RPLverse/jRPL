/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.runtime;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Runtime stack for jRPL.
 *
 * <p>This class models the execution stack where all RPL operations take place.
 * <p>Values are represented as {@code double}; booleans are encoded as
 * {@code 1.0} (true) or {@code 0.0} (false).
 *
 */
public final class ExecStack {

    // Stack storage
    private final Deque<Double> stack = new ArrayDeque<>();

    /**
     * Creates an empty execution stack to avoid JavaDoc warning.
     */
    public ExecStack() {
    }

    // Check that the stack has at least n values before an operation
    // Throws IllegalStateException with a clear message if not
    private void requireSize(int n, String op) {
        if (stack.size() < n) {
            throw new IllegalStateException(
                "Stack underflow: " + op + " requires " + n + " value" + (n == 1 ? "" : "s")
                + ", found " + stack.size()
            );
        }
    }

    /**
     * Pushes a value onto the stack.
     *
     * @param v value to push
     */
    public void push(double v) {
        stack.addLast(v);
    }

    /**
     * Pops and returns the top value.
     *
     * @return the popped value
     */
    public double pop() {
        requireSize(1, "pop()");
        return stack.removeLast();
    }

    /**
     * Returns (without popping) the top value.
     *
     * @return the value currently on top of the stack
     */
    public double peek() {
        requireSize(1, "peek()");
        return stack.peekLast();
    }

    /**
     * Duplicates the top value.
     */
    public void dup() {
        requireSize(1, "DUP");
        push(peek());
    }

    /**
     * Swaps the two topmost values.
     */
    public void swap() {
        requireSize(2, "SWAP");
        double a = pop(), b = pop();
        push(a);
        push(b);
    }

    /**
     * Drops (removes) the top value.
     */
    public void drop() {
        requireSize(1, "DROP");
        pop();
    }

    /**
     * Pushes a boolean as 1.0 (true) or 0.0 (false).
     *
     * @param b boolean value to push
     */
    public void pushBool(boolean b) {
        push(b ? 1.0 : 0.0);
    }

    /**
     * Pops two values and pushes their sum (a + b).
     */
    public void add() {
        requireSize(2, "ADD");
        push(pop() + pop());
    }

    /**
     * Pops two values and pushes their difference (a - b).
     */
    public void sub() {
        requireSize(2, "SUB");
        double b = pop(), a = pop();
        push(a - b);
    }

    /**
     * Pops two values and pushes their product (a * b).
     */
    public void mul() {
        requireSize(2, "MUL");
        push(pop() * pop());
    }

    /**
     * Pops two values and pushes their quotient (a / b).
     */
    public void div() {
        requireSize(2, "DIV");
        double b = pop(), a = pop();
        if (b == 0.0) {
            throw new ArithmeticException("Division by zero");
        }
        push(a / b);
    }

    /**
     * Pops two values and pushes the power (a ^ b).
     */
    public void pow() {
        requireSize(2, "POW");
        double e = pop(), b = pop();
        push(Math.pow(b, e));
    }

    /**
     * Pops two values and pushes 1.0 if a {@literal >} b, else 0.0.
     */
    public void cmpGT() {
        requireSize(2, ">");
        double b = pop(), a = pop();
        pushBool(a > b);
    }

    /**
     * Pops two values and pushes 1.0 if a {@literal <} b, else 0.0.
     */
    public void cmpLT() {
        requireSize(2, "<");
        double b = pop(), a = pop();
        pushBool(a < b);
    }

    /**
     * Pops two values and pushes 1.0 if a {@literal >=} b, else 0.0.
     */
    public void cmpGE() {
        requireSize(2, ">=");
        double b = pop(), a = pop();
        pushBool(a >= b);
    }

    /**
     * Pops two values and pushes 1.0 if a {@literal <=} b, else 0.0.
     */
    public void cmpLE() {
        requireSize(2, "<=");
        double b = pop(), a = pop();
        pushBool(a <= b);
    }

    /**
     * Pops two values and pushes 1.0 if a == b, else 0.0.
     */
    public void cmpEQ() {
        requireSize(2, "==");
        double b = pop(), a = pop();
        pushBool(Double.compare(a, b) == 0);
    }

    /**
     * Pops two values and pushes 1.0 if a != b, else 0.0.
     */
    public void cmpNE() {
        requireSize(2, "!=");
        double b = pop(), a = pop();
        pushBool(Double.compare(a, b) != 0);
    }

    /**
     * Returns the stack as an array (top element is last).
     *
     * @return an array copy of the stack contents
     */
    public double[] toArray() {
        return stack.stream().mapToDouble(Double::doubleValue).toArray();
    }

    /**
     * Returns the current stack size.
     *
     * @return number of items on the stack
     */
    public int size() {
        return stack.size();
    }
}
