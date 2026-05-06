package org.example;

import org.eclipse.lsp4j.*;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LogoLanguageServer implements LanguageServer {
    private final LogoTextDocumentService textDocumentService = new LogoTextDocumentService();
    private LanguageClient client;
    @Override
    public CompletableFuture<InitializeResult> initialize(InitializeParams params) {
        System.err.println("INITIALIZE");
        ServerCapabilities capabilities = new ServerCapabilities();
        capabilities.setDefinitionProvider(true);
        capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
        capabilities.setSemanticTokensProvider(new SemanticTokensWithRegistrationOptions(
                new SemanticTokensLegend(
                        List.of(
                                "procedureKeyword",
                                "loopKeyword",
                                "conditionKeyword",
                                "definitionKeyword",
                                "positionCommand",
                                "turtleQuery",
                                "penCommand",
                                "penQuery",
                                "drawingCommand",
                                "windowCommand",
                                "windowQuery",
                                "outputCommand",
                                "mathCommand",
                                "logicCommand",
                                "listCommand",
                                "controlCommand",
                                "procedureCommand",
                                "variableCommand",
                                "predicateCommand",
                                "receiverCommand",
                                SemanticTokenTypes.Function,
                                SemanticTokenTypes.Variable,
                                SemanticTokenTypes.Number,
                                SemanticTokenTypes.String,
                                SemanticTokenTypes.Comment,
                                SemanticTokenTypes.Operator,
                                "toKeyword",
                                "endKeyword"
                        ),
                        List.of(
                                SemanticTokenModifiers.Declaration,
                                SemanticTokenModifiers.DefaultLibrary
                        )
                ),
                true,
                false
        ));

        InitializeResult result= new InitializeResult(capabilities);
        return CompletableFuture.completedFuture(result);
    }

    @Override
    public CompletableFuture<Object> shutdown() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void exit() {
        System.exit(0);
    }

    @Override
    public TextDocumentService getTextDocumentService() {
        return textDocumentService;
    }

    @Override
    public WorkspaceService getWorkspaceService() {
        return null;
    }

    public void connect(LanguageClient client){
        this.client = client;
        textDocumentService.connect(client);
    }

    @Override
    public void setTrace(SetTraceParams params) {
    }

}
