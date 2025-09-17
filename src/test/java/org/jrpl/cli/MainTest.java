/*
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

    // Create a temporary directory for test input and output
    Path tmp = Files.createTempDirectory("jrpl-test-");

    // Write a small RPL program (2 3 +) into hello.rpl
    Path in = tmp.resolve("hello.rpl");
    Files.writeString(in, "<< 2 3 + >>");

    // Output directory for the generated class
    Path outDir = tmp.resolve("out");

    // Fully qualified class name of the program to generate
    String fqcn = "org.jrpl.gen.TestProg";

    // Invoke jRPL CLI to compile the source into JVM bytecode
    org.jrpl.cli.Main.main(new String[]{
        in.toString(),
        "--out-dir", outDir.toString(),
        "--class-name", fqcn
    });

    // Verify that the generated .class file actually exists
    Path classFile = outDir.resolve("org/jrpl/gen/TestProg.class");
    assertTrue(Files.exists(classFile), "Class file should exist: " + classFile);

    // Load the generated class dynamically using an isolated classloader
    try (URLClassLoader cl = new URLClassLoader(new URL[]{ outDir.toUri().toURL() })) {
      Class<?> c = Class.forName(fqcn, true, cl);

      // Test the static run(ExecStack) method
      Method run = c.getMethod("run", ExecStack.class);
      ExecStack s = new ExecStack();
      run.invoke(null, s);

      // The stack must contain exactly one element (2+3 = 5)
      assertEquals(1, s.size());
      assertEquals(5.0, s.pop(), 1e-9);

      // Test the static main(String[]) method
      Method main = c.getMethod("main", String[].class);

      // Method must exist and return void
      assertEquals(void.class, main.getReturnType());
    }
  }
}
