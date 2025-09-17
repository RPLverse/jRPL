/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.runtime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class ExecStackTest {

    @Test
    @DisplayName("push() and add() produce the expected result on the stack")
    void pushAndAddProducesExpectedArray() {
        ExecStack st = new ExecStack();

        // Push 2 and 3 [2.0, 3.0]
        st.push(2.0);
        st.push(3.0);

        // Add the top two values [5.0]
        st.add();

        // Verify the stack now contains only [5.0]
        assertArrayEquals(new double[]{5.0}, st.toArray(), 1e-9);
    }

    @Test
    @DisplayName("dup() and swap() manipulate the stack as expected")
    void dupAndSwapWorkCorrectly() {
        ExecStack st = new ExecStack();

        // Push 1 and 2 [1.0, 2.0]
        st.push(1.0);
        st.push(2.0);

        // Duplicate the top element [1.0, 2.0, 2.0]
        st.dup();

        // Swap the top two elements [1.0, 2.0, 2.0]
        st.swap();

        // Verify final stack state
        assertArrayEquals(new double[]{1.0, 2.0, 2.0}, st.toArray(), 1e-9);
    }
}
