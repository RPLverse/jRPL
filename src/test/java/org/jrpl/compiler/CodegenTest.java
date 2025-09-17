/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler;

import org.jrpl.compiler.codegen.ClassEmitter;
import org.jrpl.compiler.ir.*;
import org.jrpl.runtime.ExecStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CodegenTest {

  @Test
  @DisplayName("Emits and runs addition (+)")
  void emitsAndRunsAdd() throws Exception {

    // Build an intermediate representation (IR):
    // push 2; push 3; add
    List<Instruction> ir = List.of(
        new PushConst(2.0),
        new PushConst(3.0),
        new BinOp(BinOp.Kind.ADD)
    );

    // Temporary directory and output path for the generated class
    Path tmp = Files.createTempDirectory("jrpl-codegen-");
    String internalName = "org/jrpl/gen/IR_Add";
    Path out = tmp.resolve(internalName + ".class");

    // Emit bytecode for the IR and write it to disk
    new ClassEmitter(internalName, true).writeTo(out, ir);
    assertTrue(Files.exists(out));

    // Load the generated class with a fresh classloader
    try (URLClassLoader cl = new URLClassLoader(new URL[]{ tmp.toUri().toURL() })) {
      Class<?> c = Class.forName(internalName.replace('/', '.'), true, cl);

      // Invoke the generated run(ExecStack) method
      Method run = c.getMethod("run", ExecStack.class);
      ExecStack s = new ExecStack();
      run.invoke(null, s);

      // The stack must contain exactly one element (2+3 = 5)
      assertEquals(1, s.size());
      assertEquals(5.0, s.pop(), 1e-9);
    }
  }
}
