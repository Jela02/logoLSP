package org.example;

import org.example.parser.*;
import org.example.analysis.*;

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
   private LanguageClient client;
    private Map<String, String> documentTexts = new HashMap<>();
    private Map<String, DocumentAnalysis> documents = new HashMap<>();
    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> declaration(DeclarationParams params) {
        return TextDocumentService.super.declaration(params);
    }

    @Override
    public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>> definition(DefinitionParams params) {
        return TextDocumentService.super.definition(params);
    }

    @Override
    public void didOpen(DidOpenTextDocumentParams params) {
        System.err.println("opened");
        String text = params.getTextDocument().getText();
        String uri = params.getTextDocument().getUri();

        documentTexts.put(uri,text);
        DocumentAnalysis analysis = analyze(text);
        documents.put(uri, analysis);

    }

    @Override
    public void didChange(DidChangeTextDocumentParams params) {
        System.err.println("DIDCHANGE");
        String uri = params.getTextDocument().getUri();
        if (params.getContentChanges().isEmpty()) {
            return;
        }
        String text = params.getContentChanges().get(params.getContentChanges().size() - 1).getText();
        documentTexts.put(uri, text);
        DocumentAnalysis analysis = analyze(text);
        documents.put(uri, analysis);
    }

    @Override
    public void didClose(DidCloseTextDocumentParams params) {
        System.err.println("DIDCLOSE");
        String uri = params.getTextDocument().getUri();
        documentTexts.remove(uri);
        documents.remove(uri);
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
        List<Integer> data = new ArrayList<>();

        int prevLine = 0;
        int prevColumn = 0;

        for (int i = 0; i < tokens.size(); i++){
            LogoToken token = tokens.get(i);
            Integer tokenType = semanticTokenType(token.type);
            if(tokenType == null){
                continue;
            }
            int deltaLine = token.line - prevLine;
            int deltaColumn = deltaLine == 0 ? token.column - prevColumn : token.column;

            data.add(deltaLine);
            data.add(deltaColumn);

            data.add(token.text.length());
            data.add(tokenType);
            data.add(semanticTokenModifiers(tokens, i));

            prevLine = token.line;
            prevColumn = token.column;
        }
        return CompletableFuture.completedFuture(new SemanticTokens(data));

    }
    private static final int TOKEN_TYPE_KEYWORD = 0;
    private static final int TOKEN_TYPE_FUNCTION = 1;
    private static final int TOKEN_TYPE_VARIABLE = 2;
    private static final int TOKEN_TYPE_NUMBER = 3;

    private Integer semanticTokenType(LogoTokenType tokenType) {
        return switch (tokenType) {
            case TO, END -> TOKEN_TYPE_KEYWORD;
            case FORWARD, RIGHT, LEFT, IDENTIFIER -> TOKEN_TYPE_FUNCTION;
            case VARIABLE -> TOKEN_TYPE_VARIABLE;
            case NUMBER -> TOKEN_TYPE_NUMBER;
            default -> null;
        };
    }

    private static final int TOKEN_MODIFIER_DECLARATION = 1;
    private static final int TOKEN_MODIFIER_DEFAULT_LIBRARY = 1 << 1;

    private int semanticTokenModifiers(List<LogoToken> tokens, int index) {
        LogoToken token = tokens.get(index);
        int modifiers = 0;

        if (token.type == LogoTokenType.IDENTIFIER && index > 0 && tokens.get(index - 1).type == LogoTokenType.TO) {
            modifiers |= TOKEN_MODIFIER_DECLARATION;
        }
        if (token.type == LogoTokenType.FORWARD || token.type == LogoTokenType.RIGHT || token.type == LogoTokenType.LEFT) {
            modifiers |= TOKEN_MODIFIER_DEFAULT_LIBRARY;
        }
        if (token.type == LogoTokenType.VARIABLE && isVariableDeclaration(tokens, index)) {
            modifiers |= TOKEN_MODIFIER_DECLARATION;
        }

        return modifiers;
    }
    private boolean isVariableDeclaration(List<LogoToken> tokens, int index) {
        int line = tokens.get(index).line;
        for (int i = index - 1; i >= 0 && tokens.get(i).line == line; i--) {
            if (tokens.get(i).type == LogoTokenType.TO) {
                return true;
            }
        }
        return false;
    }
    @Override
    public CompletableFuture<DocumentDiagnosticReport> diagnostic(DocumentDiagnosticParams params) {
        return TextDocumentService.super.diagnostic(params);
    }

    public void connect(LanguageClient client){
        this.client = client;
    }

    private DocumentAnalysis analyze(String text){
        LogoLexer lexer = new LogoLexer(text);
        List<LogoToken> tokens = lexer.tokenize();

        DocumentAnalysis analysis = new DocumentAnalysis();
        analysis.analyze(tokens);

        return analysis;
    }



}
