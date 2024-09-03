# Implementation for Seamless Scope-Safe Metaprogramming through Polymorphic Subtype Inference (Short Paper)

Our artifact implements the quasiquote syntax, type inference algorithm, and code generation on the MLscript compiler.
The artifact consists of two parts:

- The main project is written in Scala and powered by sbt,
which includes the original MLscript compiler, our implementation, and corresponding test cases for quasiquote;
- The web demo allows users to compile and run general MLscript with our quasiquote system programs directly in browsers
and check type inference and execution results.

Our quasiquote system is implemented in the main project, on which the web demo is based.

We implement our system as a part of [MLscript](https://github.com/hkust-taco/mlscript) with first-class support.
Both the parser and type checker of code quotation are integrated with the MLscript compiler,
which is written in Scala.

## Getting Started

### Software Dependencies
You need [JDK supported by Scala][supported-jdk-versions], [sbt][sbt], [Node.js][node.js], and TypeScript to compile the project and run the tests.

### Installation
We recommend you to install JDK and sbt via [coursier][coursier]. The versions of Node.js that passed our tests are from v16.14 to v16.17, v17 and v18. Run `npm install` to install TypeScript. **Note that ScalaJS cannot find the global installed TypeScript.** We explicitly support TypeScript v4.7.4.

[supported-jdk-versions]: https://docs.scala-lang.org/overviews/jdk-compatibility/overview.html
[sbt]: https://www.scala-sbt.org/
[node.js]: https://nodejs.org/
[coursier]: https://get-coursier.io/

### Other Instructions
Running the main MLscript tests only requires the Scala Build Tool installed.
In the terminal, run `sbt mlscriptJVM/test`.

Running the ts2mls MLscript tests requires NodeJS, and TypeScript in addition.
In the terminal, run `sbt ts2mlsTest/test`.

You can also run all tests simultaneously.
In the terminal, run `sbt test`.

Individual tests can be run with `-z`.
For example, `~mlscriptJVM/testOnly mlscript.DiffTests -- -z parser` will watch for file changes and continuously run all parser tests (those that have "parser" in their name).

### Evaluation
Running the main MLscript tests only requires the Scala Build Tool installed.
In the terminal, run `sbt mlscriptJVM/test`.

To watch for file changes and continuously run the quasiquote tests,
execute `~mlscriptJVM/testOnly mlscript.DiffTests -- -z qq` in the sbt session.

To run the demo on your computer, compile the project with `sbt fastOptJS`, then open the `local_testing.html` file in your browser.

