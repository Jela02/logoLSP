# Logo Language Server

This project is a custom Language Server Protocol (LSP) implementation for the Logo programming language.
It is written in Java and uses the LSP4J library.

The language server is intended to be used from a custom Visual Studio Code extension. When the extension is
started with the `F5` key, VS Code opens a new Extension Development Host window where `.logo` files can use
the language features provided by this server.

## Features

- Semantic syntax highlighting for Logo code
- Go to definition for procedures
- Go to definition for variables
- Diagnostics for undefined variables
- Diagnostics for undefined procedure calls
- Basic Logo lexer support for commands, procedures, variables, numbers, strings, comments, symbols, and operators

## Requirements

- Java 21
- Maven
- Visual Studio Code, for running the custom extension

## Build

Build the server JAR with:

```bash
mvn package
```

The packaged server is created at:

```text
target/logoLSP-1.0-SNAPSHOT.jar
```

## Run the Server

The server communicates over standard input and standard output, which is the usual setup for an LSP server
started by an editor extension.

You can run the built server directly with:

```bash
java -jar target/logoLSP-1.0-SNAPSHOT.jar
```

During development, the server writes log messages to:

```text
logo-lsp.log
```

## Run with the VS Code Extension

1. Open the VS Code extension project.
2. Press `F5` to start debugging the extension.
3. VS Code opens a new Extension Development Host window.
4. Open or create a `.logo` file in that window.
5. Use the supported language features, including syntax highlighting, go to definition, and diagnostics for undefined variables and procedures.

## Test

Run the tests with:

```bash
mvn test
```

## Project Structure

```text
src/main/java/org/example
  Main.java                    LSP server entry point
  LogoLanguageServer.java      LSP capabilities
  LogoTextDocumentService.java Text document events, semantic tokens, definitions, and diagnostics

src/main/java/org/example/analysis
  DocumentAnalysis.java        Procedure and variable analysis
  ProcedureCall.java           Procedure call model
  ProcedureDefinition.java     Procedure definition model
  VariableDefinition.java      Variable definition model

src/main/java/org/example/parser
  LogoLexer.java               Logo lexer
  LogoToken.java               Token model
  LogoTokenType.java           Token types
```
