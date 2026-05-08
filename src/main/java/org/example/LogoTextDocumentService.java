package org.example;

import org.example.analysis.ProcedureDefinition;
import org.example.analysis.VariableDefinition;
import org.example.parser.*;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.example.analysis.DocumentAnalysis;
import org.example.parser.LogoLexer;
import org.example.parser.LogoToken;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LogoTextDocumentService implements TextDocumentService {
    private final Map<String, String> documentTexts = new HashMap<>();
    private final Map<String, DocumentAnalysis> documents = new HashMap<>();
    private LanguageClient client;


    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        System.err.println("opened");
        String text = params.getTextDocument().getText();
        String uri = params.getTextDocument().getUri();

        documentTexts.put(uri,text);
        DocumentAnalysis analysis = analyze(text);
        documents.put(uri, analysis);
        publishDiagnostics(uri, analysis);

    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        System.err.println("DIDCHANGE");
        String uri = params.getTextDocument().getUri();
        if (params.getContentChanges().isEmpty()) {
            return;
        }
        String text = params.getContentChanges().getLast().getText();
        documentTexts.put(uri, text);
        DocumentAnalysis analysis = analyze(text);
        documents.put(uri, analysis);
        publishDiagnostics(uri, analysis);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        System.err.println("DIDCLOSE");
        String uri = params.getTextDocument().getUri();
        documentTexts.remove(uri);
        documents.remove(uri);
        publishDiagnostics(uri, Collections.emptyList());
    }

    @Override
    public void didSave(DidSaveTextDocumentParams params) {
        System.err.println("DIDSAVE");
    }

    @Override
    public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
        String uri = params.getTextDocument().getUri();
        String text = documentTexts.get(uri);

        if(text == null) return  CompletableFuture.completedFuture((new SemanticTokens(Collections.emptyList())));

        LogoLexer lexer = new LogoLexer(text);
        List<LogoToken> tokens = lexer.tokenize();
        DocumentAnalysis analysis = new DocumentAnalysis();
        analysis.analyze(tokens);
        List<Integer> data = new ArrayList<>();

        int prevLine = 0;
        int prevColumn = 0;

        for (int i = 0; i < tokens.size(); i++){
            LogoToken token = tokens.get(i);
            Integer tokenType = semanticTokenType(analysis, tokens, i);
            if(tokenType == null){
                continue;
            }
            int deltaLine = token.line() - prevLine;
            int deltaColumn = deltaLine == 0 ? token.column() - prevColumn : token.column();

            data.add(deltaLine);
            data.add(deltaColumn);

            data.add(token.text().length());
            data.add(tokenType);
            data.add(semanticTokenModifiers(analysis, tokens, i));

            prevLine = token.line();
            prevColumn = token.column();
        }
        return CompletableFuture.completedFuture(new SemanticTokens(data));

    }
    private static final int TOKEN_TYPE_TO = 0;
    private static final int TOKEN_TYPE_END = 1;
    private static final int TOKEN_TYPE_PROCEDURE_KEYWORD = 2;
    private static final int TOKEN_TYPE_LOOP_KEYWORD = 3;
    private static final int TOKEN_TYPE_CONDITION_KEYWORD = 4;
    private static final int TOKEN_TYPE_DEFINITION_KEYWORD = 5;
    private static final int TOKEN_TYPE_VARIABLE_COMMAND = 6;
    private static final int TOKEN_TYPE_COMMAND = 7;
    private static final int TOKEN_TYPE_FUNCTION = 8;
    private static final int TOKEN_TYPE_VARIABLE = 9;
    private static final int TOKEN_TYPE_NUMBER = 10;
    private static final int TOKEN_TYPE_STRING = 11;
    private static final int TOKEN_TYPE_COMMENT = 12;
    private static final int TOKEN_TYPE_OPERATOR = 13;

    private Integer semanticTokenType(DocumentAnalysis analysis, List<LogoToken> tokens, int index) {
        LogoToken token = tokens.get(index);
        if (analysis.isVariableReference(token)) {
            return TOKEN_TYPE_VARIABLE;
        }
        return switch (token.type()) {
            case TO -> TOKEN_TYPE_TO;
            case END -> TOKEN_TYPE_END;
            case PROCEDURE_KEYWORD -> TOKEN_TYPE_PROCEDURE_KEYWORD;
            case LOOP_KEYWORD -> TOKEN_TYPE_LOOP_KEYWORD;
            case CONDITION_KEYWORD -> TOKEN_TYPE_CONDITION_KEYWORD;
            case DEFINITION_KEYWORD -> TOKEN_TYPE_DEFINITION_KEYWORD;
            case VARIABLE_COMMAND -> TOKEN_TYPE_VARIABLE_COMMAND;
            case COMMAND -> TOKEN_TYPE_COMMAND;
            case IDENTIFIER -> TOKEN_TYPE_FUNCTION;
            case VARIABLE -> TOKEN_TYPE_VARIABLE;
            case NUMBER -> TOKEN_TYPE_NUMBER;
            case STRING -> TOKEN_TYPE_STRING;
            case COMMENT -> TOKEN_TYPE_COMMENT;
            case OPERATOR, SYMBOL -> TOKEN_TYPE_OPERATOR;
            default -> null;
        };
    }

    private static final int TOKEN_MODIFIER_DECLARATION = 1;
    private static final int TOKEN_MODIFIER_DEFAULT_LIBRARY = 1 << 1;

    private int semanticTokenModifiers(DocumentAnalysis analysis, List<LogoToken> tokens, int index) {
        LogoToken token = tokens.get(index);
        int modifiers = 0;

        if (token.type() == LogoTokenType.IDENTIFIER && index > 0 && tokens.get(index - 1).type() == LogoTokenType.TO) {
            modifiers |= TOKEN_MODIFIER_DECLARATION;
        }
        if (isBuiltInFunction(token.type())) {
            modifiers |= TOKEN_MODIFIER_DEFAULT_LIBRARY;
        }
        if (analysis.isVariableDeclaration(index)) {
            modifiers |= TOKEN_MODIFIER_DECLARATION;
        }

        return modifiers;
    }
    private boolean isBuiltInFunction(LogoTokenType tokenType) {
        return tokenType == LogoTokenType.COMMAND || tokenType == LogoTokenType.VARIABLE_COMMAND;
    }

    private DocumentAnalysis analyze(String text){
        LogoLexer lexer = new LogoLexer(text);
        List<LogoToken> tokens = lexer.tokenize();

        DocumentAnalysis analysis = new DocumentAnalysis();
        analysis.analyze(tokens);

        return analysis;
    }

    public void connect(LanguageClient client) {
        this.client = client;
    }


    private int tokenLengthAt(DocumentAnalysis analysis, VariableDefinition definition) {
        LogoToken token = analysis.findTokenAt(definition.line(), definition.column());
        if (token != null) {
            return token.text().length();
        }
        return definition.name().length();
    }

    private Location location(String uri, int line, int column, int length) {
        Location location = new Location();
        location.setUri(uri);
        location.setRange(new Range(
                new Position(line, column),
                new Position(line, column + length)
        ));
        return location;
    }
    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> declaration(DeclarationParams params) {
        System.err.println("DECLARATION CALLED");
        return findDeclaration(params.getTextDocument().getUri(), params.getPosition());
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        System.err.println("DEFINITION CALLED");
        return findDeclaration(params.getTextDocument().getUri(), params.getPosition());
    }

    private CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> findDeclaration(String uri, Position pos) {
        DocumentAnalysis analysis = documents.get(uri);
        if(analysis == null){
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }

        LogoToken token = analysis.findTokenAt(pos.getLine(), pos.getCharacter());
        if (token == null){
            return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
        }
        if(token.type() == LogoTokenType.IDENTIFIER){
            ProcedureDefinition def = analysis.findProcedure(token.text());
            if(def != null){
                return CompletableFuture.completedFuture(Either.forLeft(List.of(location(uri, def.line, def.column, def.name.length()))));
            }
        }
        if(token.type() == LogoTokenType.VARIABLE){
            VariableDefinition def = analysis.findVariable(token.text(), token.line(), token.column());
            if(def != null){
                return CompletableFuture.completedFuture(Either.forLeft(List.of(location(uri, def.line(), def.column(), tokenLengthAt(analysis, def)))));
            }
        }
        return CompletableFuture.completedFuture(Either.forLeft(Collections.emptyList()));
    }

    private void publishDiagnostics(String uri, DocumentAnalysis analysis){
        List<Diagnostic> diagnostics = new ArrayList<>();

        for (LogoToken token : analysis.getUndefinedVariables()) {
            diagnostics.add(diagnostic(token, "Undefined variable: " + token.text()));
        }

        for (LogoToken token : analysis.getUndefinedProcedureCalls()) {
            diagnostics.add(diagnostic(token, "Undefined procedure: " + token.text()));
        }

        publishDiagnostics(uri, diagnostics);
    }

    private void publishDiagnostics(String uri, List<Diagnostic> diagnostics) {
        if (client != null) {
            client.publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
        }
    }

    private Diagnostic diagnostic(LogoToken token, String message) {
        Diagnostic diagnostic = new Diagnostic();
        diagnostic.setRange(new Range(
                new Position(token.line(), token.column()),
                new Position(token.line(), token.column() + token.text().length())
        ));
        diagnostic.setSeverity(DiagnosticSeverity.Error);
        diagnostic.setSource("logo-lsp");
        diagnostic.setMessage(message);
        return diagnostic;
    }
}
