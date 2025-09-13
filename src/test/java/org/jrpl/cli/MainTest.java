/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import org.jrpl.runtime.ExecStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

  @Test
  @DisplayName("Compiles RPL to a runnable class with run(ExecStack) and main(String[])")
  void compilesAndRuns() throws Exception {
    Path tmp = Files.createTempDirectory("jrpl-test-");
    Path in = tmp.resolve("hello.rpl");
    Files.writeString(in, "<< 2 3 + >>");
    Path outDir = tmp.resolve("out");

    String fqcn = "org.jrpl.gen.TestProg";
    org.jrpl.cli.Main.main(new String[]{
        in.toString(),
        "--out-dir", outDir.toString(),
        "--class-name", fqcn
    });

    Path classFile = outDir.resolve("org/jrpl/gen/TestProg.class");
    assertTrue(Files.exists(classFile), "Class file should exist: " + classFile);

    try (URLClassLoader cl = new URLClassLoader(new URL[]{ outDir.toUri().toURL() })) {
      Class<?> c = Class.forName(fqcn, true, cl);

      // public static void run(ExecStack)
      Method run = c.getMethod("run", ExecStack.class);
      ExecStack s = new ExecStack();
      run.invoke(null, s);
      assertEquals(1, s.size());
      assertEquals(5.0, s.pop(), 1e-9);

      // public static void main(String[])
      Method main = c.getMethod("main", String[].class);
      assertEquals(void.class, main.getReturnType());
    }
  }
}
