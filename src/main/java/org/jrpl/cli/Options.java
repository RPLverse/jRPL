/*
 * Copyright (c) 2025 Massimo Costantini.
 * Licensed under the Apache License, Version 2.0.
 * See the LICENSE file in the project root for full license information.
 */

package org.jrpl.cli;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Minimal command-line options parser for jRPL
// Recognized flags:
//   --out-dir <dir>
//   --class-name <BinaryName>
//   --no-main
// Unknown flags cause a usage error
final class Options {

  // Input .rpl file path (required)
  final Path inputFile;

  // Output directory where the generated .class will be written
  final Path outDir;

  // Fully qualified binary class name (optional; auto-generated if null)
  final String classBinaryName;

  // Whether to generate public static void main(String[])
  final boolean withMain;

  Options(Path inputFile, Path outDir, String classBinaryName, boolean withMain) {
    this.inputFile = inputFile;
    this.outDir = outDir;
    this.classBinaryName = classBinaryName;
    this.withMain = withMain;
  }

  // Parses command-line arguments
  // Usage: jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>] [--no-main]
  static Options parse(String[] args) {

    // Require at least the input file
    if (args.length == 0) {
      usageAndExit("Missing input file.");
    }

    Path input = Path.of(args[0]);
    Path outDir = Path.of("build/gen-classes");
    String classBinaryName = null;
    boolean withMain = true;

    // Collect remaining args for sequential processing
    List<String> rest = new ArrayList<>();
    for (int i = 1; i < args.length; i++) {
      rest.add(args[i]);
    }

    // Parse flags in order; unknown options trigger usage
    for (int i = 0; i < rest.size(); i++) {
      String a = rest.get(i);
      switch (a) {
        case "--out-dir" -> {
          if (i + 1 >= rest.size()) usageAndExit("--out-dir requires a value");
          outDir = Path.of(rest.get(++i));
        }
        case "--class-name" -> {
          if (i + 1 >= rest.size()) usageAndExit("--class-name requires a value");
          classBinaryName = rest.get(++i);
        }
        case "--no-main" -> withMain = false;
        default -> usageAndExit("Unknown option: " + a);
      }
    }

    return new Options(input, outDir, classBinaryName, withMain);
  }

  // Prints usage information and exits with a non-zero code
  static void usageAndExit(String msg) {
    System.err.println(msg);
    System.out.println("""
        Usage: jrpl <file.rpl> [--out-dir <dir>] [--class-name <BinaryName>] [--no-main]
        """);
    System.exit(1);
  }
}
