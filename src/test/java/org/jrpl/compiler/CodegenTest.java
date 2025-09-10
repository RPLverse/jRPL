/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.compiler;

import org.jrpl.compiler.codegen.ClassEmitter;
import org.jrpl.compiler.ir.*;
import org.jrpl.runtime.ExecStack;
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
  void emitsAndRunsAdd() throws Exception {
    // IR: push 2; push 3; add
    List<Instruction> ir = List.of(
        new PushConst(2.0),
        new PushConst(3.0),
        new BinOp(BinOp.Kind.ADD)
    );

    Path tmp = Files.createTempDirectory("jrpl-codegen-");
    String internalName = "org/jrpl/gen/IR_Add";
    Path out = tmp.resolve(internalName + ".class");

    new ClassEmitter(internalName, true).writeTo(out, ir);
    assertTrue(Files.exists(out));

    try (URLClassLoader cl = new URLClassLoader(new URL[]{ tmp.toUri().toURL() })) {
      Class<?> c = Class.forName(internalName.replace('/', '.'), true, cl);
      Method run = c.getMethod("run", ExecStack.class);
      ExecStack s = new ExecStack();
      run.invoke(null, s);
      assertEquals(1, s.size());
      assertEquals(5.0, s.pop(), 1e-9);
    }
  }
}
