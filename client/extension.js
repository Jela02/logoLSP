const { LanguageClient, TransportKind } = require('vscode-languageclient/node');

let client;

function activate(context) {

    const serverOptions = {
        command: 'java',
        args: [
            '-jar',
            '../target/logoLSP-1.0-SNAPSHOT.jar'
        ],
        transport: TransportKind.stdio
    };

    const clientOptions = {
        documentSelector: [
            { scheme: 'file', language: 'logo' }
        ],
        outputChannelName: 'Logo LSP'
    };

    client = new LanguageClient(
        'logoLsp',
        'Logo LSP Server',
        serverOptions,
        clientOptions
    );

    client.start();

    console.log('Logo LSP klijent pokrenut');
}

function deactivate() {
    if (client) {
        return client.stop();
    }
}

module.exports = { activate, deactivate };
