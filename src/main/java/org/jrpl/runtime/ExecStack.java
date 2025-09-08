/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.runtime;

import java.util.Stack;

/**
 * Runtime stack for jRPL.
 * <p>
 * This class models the execution stack where all RPL operations take place.
 * Values are represented as {@code double}; booleans are encoded as
 * {@code 1.0} (true) or {@code 0.0} (false).
 * </p>
 */
public final class ExecStack {

    /** Underlying stack storage. */
    private final Stack<Double> stack = new Stack<>();

    /** Creates an empty stack. */
    public ExecStack() {}

    /**
     * Pushes a value onto the stack.
     *
     * @param v value to push
     */
    public void push(double v) { stack.push(v); }

    /**
     * Pops and returns the top value.
     *
     * @return the popped value
     */
    public double pop() { return stack.pop(); }

    /**
     * Returns (without popping) the top value.
     *
     * @return the value currently on top of the stack
     */
    public double peek() { return stack.peek(); }

    /** Duplicates the top value. */
    public void dup() { push(peek()); }

    /** Swaps the two topmost values. */
    public void swap() { double a = pop(), b = pop(); push(a); push(b); }

    /** Drops (removes) the top value. */
    public void drop() { pop(); }

    /**
     * Pushes a boolean as 1.0 (true) or 0.0 (false).
     *
     * @param b boolean value to push
     */
    public void pushBool(boolean b) { push(b ? 1.0 : 0.0); }

    /** Pops two values and pushes their sum (a + b). */
    public void add() { push(pop() + pop()); }

    /** Pops two values and pushes their difference (a - b). */
    public void sub() { double b = pop(), a = pop(); push(a - b); }

    /** Pops two values and pushes their product (a * b). */
    public void mul() { push(pop() * pop()); }

    /** Pops two values and pushes their quotient (a / b). */
    public void div() { double b = pop(), a = pop(); push(a / b); }

    /** Pops two values and pushes the power (a ^ b). */
    public void pow() { double e = pop(), b = pop(); push(Math.pow(b, e)); }

    /** Pops two values and pushes 1.0 if a {@literal >} b, else 0.0. */
    public void cmpGT() { double b = pop(), a = pop(); pushBool(a > b); }

    /** Pops two values and pushes 1.0 if a {@literal <} b, else 0.0. */
    public void cmpLT() { double b = pop(), a = pop(); pushBool(a < b); }

    /** Pops two values and pushes 1.0 if a {@literal >=} b, else 0.0. */
    public void cmpGE() { double b = pop(), a = pop(); pushBool(a >= b); }

    /** Pops two values and pushes 1.0 if a {@literal <=} b, else 0.0. */
    public void cmpLE() { double b = pop(), a = pop(); pushBool(a <= b); }

    /** Pops two values and pushes 1.0 if a == b, else 0.0. */
    public void cmpEQ() { double b = pop(), a = pop(); pushBool(Double.compare(a, b) == 0); }

    /** Pops two values and pushes 1.0 if a != b, else 0.0. */
    public void cmpNE() { double b = pop(), a = pop(); pushBool(Double.compare(a, b) != 0); }

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
    public int size() { return stack.size(); }
}
