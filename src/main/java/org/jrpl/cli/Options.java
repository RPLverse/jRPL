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

  // Parsed CLI options: input file, output directory, class name (optional) and main flag
  final Path inputFile;
  final Path outDir;
  final String classBinaryName;
  final boolean withMain;

  // Creates a new set of CLI options with the given values
  Options(Path inputFile, Path outDir, String classBinaryName, boolean withMain) {

    // Initialize fields from constructor parameters
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
          throw new IllegalArgumentException("Missing input file");
      }

      // Initialize defaults: input file from args[0], output dir, class name and main flag
      Path input = Path.of(args[0]);
      Path outDir = Path.of("build/gen-classes");
      String classBinaryName = null;
      boolean withMain = true;

      // Collect remaining args for sequential processing
      List<String> rest = new ArrayList<>();
      for (int i = 1; i < args.length; i++) {
          rest.add(args[i]);
      }

      // Parse flags in order, unknown options trigger usage
      for (int i = 0; i < rest.size(); i++) {
          String a = rest.get(i);
          switch (a) {
              case "--out-dir" -> {
                  if (i + 1 >= rest.size()) {
                      throw new IllegalArgumentException("--out-dir requires a value");
                  }
                  outDir = Path.of(rest.get(++i));
              }
              case "--class-name" -> {
                  if (i + 1 >= rest.size()) {
                      throw new IllegalArgumentException("--class-name requires a value");
                  }
                  classBinaryName = rest.get(++i);
              }
              case "--no-main" -> withMain = false;
              default -> throw new IllegalArgumentException("Unknown option: " + a);
          }
      }

      // Build and return the parsed options
      return new Options(input, outDir, classBinaryName, withMain);
  }
}
