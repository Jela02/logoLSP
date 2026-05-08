# Logo Language Server

A custom implementation of the Language Server Protocol (LSP) for the ___Logo___ programming language.

The project consists of:

- __Server__ — written in Java using LSP4J
- __Client__ — a custom Visual Studio Code extension written in JavaScript

The server communicates with the VS Code extension over standard input/output.

## Features

- Semantic syntax highlighting for Logo code
- Go to definition for procedures
- Go to definition for variables
- Diagnostics for undefined variables
- Diagnostics for undefined procedure calls
- Basic Logo lexer support for commands, procedures, variables, numbers, strings, comments, symbols, and operators

## Requirements

Before running the project, ensure the following tools are installed:
- Java 21
- Maven
- Node.js and npm
- Visual Studio Code

## Running the LSP from IntelliJ IDEA
1. Open the project in IntelliJ IDEA
2. Run the `Run LSP in VSCode` run configuration

The configuration will:

- build the language server
- install the client dependencies
- launch VS Code with the extension loaded and open the `./sample` folder

## Running the LSP from VSCode
1. Build the language server
```bash
mvn package
```
2. Install the required dependencies
```bash
npm install ./client
```
3. Launch the extension development host and open the `./sample` folder
```
code --extensionDevelopmentPath="$PWD/client" "./sample"
```
Or run the `Run LSP extension` configuration.

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
