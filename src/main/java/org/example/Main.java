package org.example;

import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher;
import org.eclipse.lsp4j.services.LanguageClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private static final Path LOG_PATH = Path.of("logo-lsp.log");

    private static void log(String message) {
        try {
            Files.writeString(
                    LOG_PATH,
                    message + System.lineSeparator(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {
        }
        System.err.println(message);
    }

    private static void log(Throwable error) {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        log(writer.toString());
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> {
            log("Uncaught error on thread " + thread.getName());
            log(error);
        });

        log("Logo LSP server started");

        LogoLanguageServer server = new LogoLanguageServer();

        Launcher<LanguageClient> launcher =
                LSPLauncher.createServerLauncher(server, System.in, System.out);

        LanguageClient client = launcher.getRemoteProxy();
        server.connect(client);

        try {
            launcher.startListening().get();
            log("LSP listener finished normally");
        } catch (Exception e) {
            log("LSP listener terminated with exception");
            log(e);
            System.exit(1);
        }
    }
}
