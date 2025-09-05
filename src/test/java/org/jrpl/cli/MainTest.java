/**
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

  // Test following the AAA (Arrange–Act–Assert) pattern
  @Test
  @DisplayName("Generates and loads a no-op class with run() and main()")
  void generatesAndLoadsNoOpClassWithRunAndMain() throws Exception {

    // Arrange: prepare a temporary .rpl input and output directory
    Path tmp = Files.createTempDirectory("jrpl-test-");
    Path in = tmp.resolve("hello.rpl");
    Files.writeString(in, "<< 2 3 + >>"); // content unused for now
    Path outDir = tmp.resolve("out");

    // Act: call the CLI to generate the .class file (with explicit class name)
    String fqcn = "org.jrpl.gen.TestNoOp";
    org.jrpl.cli.Main.main(new String[]{
        in.toString(),
        "--out-dir", outDir.toString(),
        "--class-name", fqcn
    });

    // Assert A: class file exists
    Path classFile = outDir.resolve("org/jrpl/gen/TestNoOp.class");
    assertTrue(Files.exists(classFile), "Class file should exist: " + classFile);

    // Assert B: load class and invoke run()
    try (URLClassLoader cl = new URLClassLoader(new URL[]{ outDir.toUri().toURL() })) {
      Class<?> c = Class.forName(fqcn, true, cl);

      // Verify that the method exists and is `public static void run()`
      Method run = c.getMethod("run");
      assertEquals(void.class, run.getReturnType(), "run() should return void");
      run.invoke(null); // should not throw

      // Verify that the method exists and is `public static void main(String[] args)`
      Method main = c.getMethod("main", String[].class);
      assertEquals(void.class, main.getReturnType(), "main(String[]) should return void");

      // Cast to Object so the String[] is treated as one parameter, not varargs
      main.invoke(null, (Object) new String[0]); // should not throw
    }
  }
}
