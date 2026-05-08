package org.example.parser;

public record LogoToken(LogoTokenType type, String text, int line, int column) {
    @Override
    public String toString() {
        return type + " " + text + "(" + line + "," + column + ")";
    }
}
