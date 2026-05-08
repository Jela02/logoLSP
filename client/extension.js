const { LanguageClient, TransportKind } = require('vscode-languageclient/node');

// client je globalna varijabla jer je trebamo i pri gašenju
let client;

// activate se poziva kad VS Code otvori .logo fajl
function activate(context) {

    // Opcije za server — kako da ga pokrenemo
    const serverOptions = {
        command: 'java',
        args: [
            '-jar',
            'C:/Users/jelen/IdeaProjects/logoLSP/target/logoLSP-1.0-SNAPSHOT.jar'
        ],
        transport: TransportKind.stdio
    };

    // Opcije za klijenta — za koji jezik da se aktivira
    const clientOptions = {
        documentSelector: [
            { scheme: 'file', language: 'logo' }
        ],
        outputChannelName: 'Logo LSP'
    };

    // Kreiraj klijenta koji spaja VS Code i naš server
    client = new LanguageClient(
        'logoLsp',           // interni ID
        'Logo LSP Server',   // naziv koji se vidi u Output kanalu
        serverOptions,
        clientOptions
    );

    // Pokreni klijenta (on automatski pokreće i server)
    client.start();

    console.log('Logo LSP klijent pokrenut');
}

// deactivate se poziva kad VS Code zatvori ekstenziju
function deactivate() {
    if (client) {
        return client.stop();
    }
}

module.exports = { activate, deactivate };
